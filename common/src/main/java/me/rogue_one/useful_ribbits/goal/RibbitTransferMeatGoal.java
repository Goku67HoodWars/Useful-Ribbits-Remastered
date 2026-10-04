package me.rogue_one.useful_ribbits.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public class RibbitTransferMeatGoal extends Goal {
   private final Mob mob;
   private final double speed;
   private final int searchRange;
   private BlockPos smokerPos;
   private BlockPos chestPos;
   private RibbitTransferMeatGoal.TaskState currentTask = RibbitTransferMeatGoal.TaskState.SEARCHING;
   private Item currentMeatType;
   private int carriedCookedAmount = 0;
   private int carriedRawAmount = 0;
   private int neededRawAmount = 0;
   private int tickCounter = 0;
   private int checkTimer = 0;
   private int searchCooldown = 0;
   private int stuckTicks = 0;
   private RibbitTransferMeatGoal.TaskState lastMoveTask = null;
   private boolean hasPlayedOpenSound = false;
   private static final Set<Item> RAW_FOODS = Set.of(
      Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.MUTTON, Items.RABBIT, Items.COD, Items.SALMON
   );
   private static final Map<Item, Item> COOKING_MAP = Map.of(
      Items.BEEF,
      Items.COOKED_BEEF,
      Items.PORKCHOP,
      Items.COOKED_PORKCHOP,
      Items.CHICKEN,
      Items.COOKED_CHICKEN,
      Items.MUTTON,
      Items.COOKED_MUTTON,
      Items.RABBIT,
      Items.COOKED_RABBIT,
      Items.COD,
      Items.COOKED_COD,
      Items.SALMON,
      Items.COOKED_SALMON
   );

   public RibbitTransferMeatGoal(Mob mob, double speed, int searchRange) {
      this.mob = mob;
      this.speed = speed;
      this.searchRange = searchRange;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   @Override
   public boolean canUse() {
      if (!this.isDaytime()) {
         return false;
      }

      if (this.currentTask == RibbitTransferMeatGoal.TaskState.SEARCHING) {
         if (this.searchCooldown > 0) {
            this.searchCooldown--;
            return false;
         }
         this.searchCooldown = 20;
         this.findSmoker();
      }

      return this.smokerPos != null;
   }

   @Override
   public boolean canContinueToUse() {
      return this.isDaytime() && this.smokerPos != null;
   }

   @Override
   public void start() {
      this.currentTask = RibbitTransferMeatGoal.TaskState.GOING_TO_SMOKER;
      this.resetVariables();
   }

   @Override
   public void stop() {
      this.currentTask = RibbitTransferMeatGoal.TaskState.SEARCHING;
      this.mob.getNavigation().stop();
      this.resetVariables();
   }

   @Override
   public void tick() {
      this.tickCounter++;
      this.checkTimer++;
      if (this.isStuckState(this.currentTask)) {
         if (this.currentTask != this.lastMoveTask) {
            this.lastMoveTask = this.currentTask;
            this.stuckTicks = 0;
         } else if (++this.stuckTicks > 300) {
            // Wedged trying to reach a target for ~15s: drop it and re-plan. Carried food is
            // kept by resetVariables(), so nothing is lost.
            this.stuckTicks = 0;
            this.mob.getNavigation().stop();
            this.smokerPos = null;
            this.chestPos = null;
            this.currentTask = RibbitTransferMeatGoal.TaskState.SEARCHING;
            return;
         }
      } else {
         this.stuckTicks = 0;
         this.lastMoveTask = null;
      }
      switch (this.currentTask) {
         case GOING_TO_SMOKER:
            this.handleGoingToSmoker();
            break;
         case AT_SMOKER_WAITING:
            this.handleAtSmokerWaiting();
            break;
         case GOING_TO_CHEST:
            this.handleGoingToChest();
            break;
         case AT_CHEST_WORKING:
            this.handleAtChestWorking();
            break;
         case RETURNING_TO_SMOKER:
            this.handleReturningToSmoker();
            break;
         case RETURNING_EXCESS:
            this.handleReturningExcess();
            break;
         case WORKING_AT_SMOKER:
            this.handleWorkingAtSmoker();
      }
   }

   private void resetVariables() {
      // currentMeatType + carriedCookedAmount + carriedRawAmount are intentionally NOT reset:
      // they are items already removed from the world, so zeroing them on stop() (dusk/unload)
      // would delete them. Kept so the haul resumes next cycle (mirrors the farmer's seedCount).
      this.neededRawAmount = 0;
      this.checkTimer = 0;
      this.hasPlayedOpenSound = false;
      this.chestPos = null;
   }

   private boolean isStuckState(RibbitTransferMeatGoal.TaskState t) {
      return t == RibbitTransferMeatGoal.TaskState.GOING_TO_SMOKER
          || t == RibbitTransferMeatGoal.TaskState.GOING_TO_CHEST
          || t == RibbitTransferMeatGoal.TaskState.RETURNING_TO_SMOKER
          || t == RibbitTransferMeatGoal.TaskState.RETURNING_EXCESS;
   }

   private boolean isDaytime() {
      long timeOfDay = this.mob.level().getOverworldClockTime() % 24000L;
      return timeOfDay >= 0L && timeOfDay < 13000L;
   }

   private void findSmoker() {
      BlockPos mobPos = this.mob.blockPosition();
      Level level = this.mob.level();
      this.smokerPos = null;

      // Collect every smoker in range that actually has work to do (room for more raw food, or a
      // cooked output waiting to be collected). A full, still-cooking smoker is skipped so chefs
      // don't pile onto it. Track separately which of those are fuelled (lit, or fuel in slot 1).
      List<BlockPos> candidates = new ArrayList<>();
      List<BlockPos> fuelled = new ArrayList<>();
      for (int x = -this.searchRange; x <= this.searchRange; x++) {
         for (int y = -3; y <= 3; y++) {
            for (int z = -this.searchRange; z <= this.searchRange; z++) {
               BlockPos pos = mobPos.offset(x, y, z);
               BlockState state = level.getBlockState(pos);
               if (!state.is(Blocks.SMOKER) || !(level.getBlockEntity(pos) instanceof SmokerBlockEntity smoker)) {
                  continue;
               }

               ItemStack input = smoker.getItem(0);
               boolean inputHasRoom = input.isEmpty() || input.getCount() < input.getMaxStackSize();
               boolean outputWaiting = !smoker.getItem(2).isEmpty();
               if (!inputHasRoom && !outputWaiting) {
                  continue;
               }

               BlockPos immutable = pos.immutable();
               candidates.add(immutable);
               boolean lit = state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);
               if (lit || !smoker.getItem(1).isEmpty()) {
                  fuelled.add(immutable);
               }
            }
         }
      }

      if (candidates.isEmpty()) {
         return;
      }

      // Prefer smokers that can actually cook right now (lit, or fuel in the fuel slot) -- loading
      // raw food into an unfuelled smoker just stalls it. Fall back to any candidate only if none
      // are fuelled.
      List<BlockPos> preferred = fuelled.isEmpty() ? candidates : fuelled;

      // Within that tier, spread chefs out: favour a smoker no other chef is already standing at,
      // and random-pick within the pool so chefs searching on the same tick don't all grab one.
      List<BlockPos> unclaimed = new ArrayList<>();
      for (BlockPos pos : preferred) {
         AABB box = new AABB(pos).inflate(1.5);
         boolean claimed = !level.getEntitiesOfClass(Mob.class, box,
               other -> other != this.mob && other.getClass() == this.mob.getClass()).isEmpty();
         if (!claimed) {
            unclaimed.add(pos);
         }
      }

      List<BlockPos> pool = unclaimed.isEmpty() ? preferred : unclaimed;
      this.smokerPos = pool.get(this.mob.getRandom().nextInt(pool.size()));
   }

   private BlockPos findNearestChestWithRawFood(BlockPos center) {
      BlockPos best = null;
      double bestDist = Double.MAX_VALUE;
      for (int x = -this.searchRange; x <= this.searchRange; x++) {
         for (int y = -3; y <= 3; y++) {
            for (int z = -this.searchRange; z <= this.searchRange; z++) {
               BlockPos pos = center.offset(x, y, z);
               if (this.mob.level().getBlockState(pos).is(UsefulRibbitsModBlocks.RIBBIT_CHEST)
                  && this.mob.level().getBlockEntity(pos) instanceof Container container
                  && this.hasRawFoodInContainer(container)) {
                  double d = this.mob.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                  if (d < bestDist) {
                     bestDist = d;
                     best = pos.immutable();
                  }
               }
            }
         }
      }

      return best;
   }

   private BlockPos findNearestChest(BlockPos center) {
      BlockPos best = null;
      double bestDist = Double.MAX_VALUE;
      for (int x = -this.searchRange; x <= this.searchRange; x++) {
         for (int y = -3; y <= 3; y++) {
            for (int z = -this.searchRange; z <= this.searchRange; z++) {
               BlockPos pos = center.offset(x, y, z);
               if (this.mob.level().getBlockState(pos).is(UsefulRibbitsModBlocks.RIBBIT_CHEST)) {
                  double d = this.mob.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                  if (d < bestDist) {
                     bestDist = d;
                     best = pos.immutable();
                  }
               }
            }
         }
      }

      return best;
   }

   private boolean hasRawFoodInContainer(Container container) {
      for (int i = 0; i < container.getContainerSize(); i++) {
         ItemStack stack = container.getItem(i);
         if (!stack.isEmpty() && RAW_FOODS.contains(stack.getItem())) {
            return true;
         }
      }

      return false;
   }

   private void handleGoingToSmoker() {
      if (this.mob.distanceToSqr(this.smokerPos.getX(), this.smokerPos.getY(), this.smokerPos.getZ()) < 2.5) {
         this.currentTask = RibbitTransferMeatGoal.TaskState.AT_SMOKER_WAITING;
      } else {
         this.navigateTo(this.smokerPos);
      }
   }

   private void handleAtSmokerWaiting() {
      if (this.checkTimer % 20 != 0) {
         return;
      }
      if (this.mob.level().getBlockEntity(this.smokerPos) instanceof SmokerBlockEntity smokerEntity) {
         ItemStack output = smokerEntity.getItem(2);
         if (!output.isEmpty()) {
            BlockPos chest = this.findNearestChest(this.smokerPos);
            if (chest != null) { // only pull cooked output when there is somewhere to deposit it
               this.carriedCookedAmount = output.getCount();
               this.currentMeatType = this.getCookedMeatRawType(output.getItem());
               smokerEntity.setItem(2, ItemStack.EMPTY);
               this.chestPos = chest;
               this.currentTask = RibbitTransferMeatGoal.TaskState.GOING_TO_CHEST;
            }
            return;
         }

         this.chestPos = this.findNearestChestWithRawFood(this.smokerPos);
         if (this.chestPos != null) {
            this.calculateNeeds(smokerEntity);
            if (this.neededRawAmount > 0 || this.carriedCookedAmount > 0) {
               this.currentTask = RibbitTransferMeatGoal.TaskState.GOING_TO_CHEST;
               return;
            }
         }

         // Nothing to do at this smoker right now (busy cooking, or no raw food to fetch). Re-scan
         // for a smoker that needs a chef, so idle chefs migrate to a newly placed/fuelled smoker
         // or one that just ran dry instead of camping this one forever.
         if (this.carriedCookedAmount == 0 && this.carriedRawAmount == 0) {
            BlockPos current = this.smokerPos;
            this.findSmoker();
            if (this.smokerPos == null) {
               this.smokerPos = current;
            } else if (!this.smokerPos.equals(current)) {
               this.currentTask = RibbitTransferMeatGoal.TaskState.GOING_TO_SMOKER;
            }
         }
      }
   }

   private Item getCookedMeatRawType(Item cookedItem) {
      for (Map.Entry<Item, Item> entry : COOKING_MAP.entrySet()) {
         if (entry.getValue() == cookedItem) {
            return entry.getKey();
         }
      }

      return null;
   }

   private void calculateNeeds(SmokerBlockEntity smokerEntity) {
      ItemStack currentInput = smokerEntity.getItem(0);
      if (this.currentMeatType == null && this.chestPos != null && this.mob.level().getBlockEntity(this.chestPos) instanceof Container container) {
         this.currentMeatType = this.findFirstRawFoodType(container);
      }

      if (this.currentMeatType == null) {
         this.neededRawAmount = 0;
      } else if (currentInput.isEmpty()) {
         this.neededRawAmount = 64;
      } else if (currentInput.getItem() == this.currentMeatType) {
         this.neededRawAmount = 64 - currentInput.getCount();
      } else {
         this.neededRawAmount = 0;
      }
   }

   private Item findFirstRawFoodType(Container container) {
      for (int i = 0; i < container.getContainerSize(); i++) {
         ItemStack stack = container.getItem(i);
         if (!stack.isEmpty() && RAW_FOODS.contains(stack.getItem())) {
            return stack.getItem();
         }
      }

      return null;
   }

   private void handleGoingToChest() {
      if (this.mob.distanceToSqr(this.chestPos.getX() + 0.5, this.chestPos.getY(), this.chestPos.getZ() + 0.5) < 1.5) {
         this.currentTask = RibbitTransferMeatGoal.TaskState.AT_CHEST_WORKING;
         if (!this.hasPlayedOpenSound) {
            this.mob.level().playSound(null, this.chestPos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, this.mob.level().getRandom().nextFloat() * 0.1F + 0.9F);
            this.hasPlayedOpenSound = true;
         }
      } else {
         this.navigateTo(this.chestPos);
      }
   }

   private void handleAtChestWorking() {
      if (this.mob.level().getBlockEntity(this.chestPos) instanceof Container container) {
         if (this.carriedCookedAmount > 0) {
            Item cookedItem = COOKING_MAP.get(this.currentMeatType);
            if (cookedItem != null) {
               ItemStack toStore = new ItemStack(cookedItem, this.carriedCookedAmount);
               this.storeItemInContainer(container, toStore);
               this.carriedCookedAmount = toStore.getCount(); // keep whatever did not fit
            }
         }

         if (this.neededRawAmount > 0) {
            if (this.getTotalItemsInContainer(container, this.currentMeatType) == 0) {
               this.currentMeatType = this.findFirstRawFoodType(container);
               if (this.currentMeatType != null) {
                  if (this.mob.level().getBlockEntity(this.smokerPos) instanceof SmokerBlockEntity smokerEntity) {
                     this.calculateNeeds(smokerEntity);
                  }
               } else {
                  this.neededRawAmount = 0;
               }
            }

            if (this.neededRawAmount > 0 && this.currentMeatType != null) {
               int availableInChest = this.getTotalItemsInContainer(container, this.currentMeatType);
               int toTake = Math.min(this.neededRawAmount, availableInChest);
               this.carriedRawAmount = this.extractItemsFromContainer(container, this.currentMeatType, toTake);
            }
         }

         this.mob.level().playSound(null, this.chestPos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, this.mob.level().getRandom().nextFloat() * 0.1F + 0.9F);
         this.hasPlayedOpenSound = false;
         if (this.carriedRawAmount > 0) {
            this.currentTask = RibbitTransferMeatGoal.TaskState.RETURNING_TO_SMOKER;
         } else {
            this.currentTask = RibbitTransferMeatGoal.TaskState.AT_SMOKER_WAITING;
            this.navigateTo(this.smokerPos);
         }
      }
   }

   private void handleReturningToSmoker() {
      if (this.mob.level().getBlockEntity(this.smokerPos) instanceof SmokerBlockEntity smokerEntity) {
         ItemStack currentInput = smokerEntity.getItem(0);
         int currentNeeded;
         if (currentInput.isEmpty()) {
            currentNeeded = 64;
         } else if (currentInput.getItem() == this.currentMeatType) {
            currentNeeded = 64 - currentInput.getCount();
         } else {
            currentNeeded = 0;
         }

         if (this.carriedRawAmount > currentNeeded) {
            this.currentTask = RibbitTransferMeatGoal.TaskState.RETURNING_EXCESS;
            return;
         }
      }

      if (this.mob.distanceToSqr(this.smokerPos.getX(), this.smokerPos.getY(), this.smokerPos.getZ()) < 2.5) {
         this.currentTask = RibbitTransferMeatGoal.TaskState.WORKING_AT_SMOKER;
      } else {
         this.navigateTo(this.smokerPos);
      }
   }

   private void handleReturningExcess() {
      if (this.mob.distanceToSqr(this.chestPos.getX() + 0.5, this.chestPos.getY(), this.chestPos.getZ() + 0.5) < 1.5) {
         if (this.mob.level().getBlockEntity(this.chestPos) instanceof Container container
            && this.mob.level().getBlockEntity(this.smokerPos) instanceof SmokerBlockEntity smokerEntity) {
            ItemStack currentInput = smokerEntity.getItem(0);
            int currentNeeded;
            if (currentInput.isEmpty()) {
               currentNeeded = 64;
            } else if (currentInput.getItem() == this.currentMeatType) {
               currentNeeded = 64 - currentInput.getCount();
            } else {
               currentNeeded = 0;
            }

            int toReturn = this.carriedRawAmount - currentNeeded;
            if (toReturn > 0) {
               ItemStack excessStack = new ItemStack(this.currentMeatType, toReturn);
               this.storeItemInContainer(container, excessStack);
               this.carriedRawAmount -= (toReturn - excessStack.getCount()); // only what actually fit
            }
         }

         this.currentTask = RibbitTransferMeatGoal.TaskState.RETURNING_TO_SMOKER;
      } else {
         this.navigateTo(this.chestPos);
      }
   }

   private void handleWorkingAtSmoker() {
      if (this.mob.level().getBlockEntity(this.smokerPos) instanceof SmokerBlockEntity smokerEntity) {
         if (this.carriedRawAmount > 0) {
            ItemStack currentInput = smokerEntity.getItem(0);
            if (currentInput.isEmpty()) {
               smokerEntity.setItem(0, new ItemStack(this.currentMeatType, this.carriedRawAmount));
               this.carriedRawAmount = 0;
            } else if (currentInput.getItem() == this.currentMeatType) {
               int canAdd = 64 - currentInput.getCount();
               int toAdd = Math.min(this.carriedRawAmount, canAdd);
               currentInput.grow(toAdd);
               smokerEntity.setItem(0, currentInput);
               this.carriedRawAmount -= toAdd;
            }
         }

         ItemStack output = smokerEntity.getItem(2);
         ItemStack input = smokerEntity.getItem(0);
         if (output.getCount() >= 64 || input.isEmpty() && output.getCount() > 0) {
            this.carriedCookedAmount = output.getCount();
            smokerEntity.setItem(2, ItemStack.EMPTY);
            this.chestPos = this.findNearestChest(this.smokerPos);
            if (this.chestPos != null) {
               this.currentTask = RibbitTransferMeatGoal.TaskState.GOING_TO_CHEST;
               this.neededRawAmount = 0;
            } else {
               this.currentTask = RibbitTransferMeatGoal.TaskState.AT_SMOKER_WAITING;
            }
         } else if (input.isEmpty() && output.isEmpty()) {
            this.currentTask = RibbitTransferMeatGoal.TaskState.AT_SMOKER_WAITING;
            this.currentMeatType = null;
         }
      }
   }

   private void navigateTo(BlockPos pos) {
      PathNavigation navigation = this.mob.getNavigation();
      navigation.moveTo(pos.getX(), pos.getY(), pos.getZ(), this.speed);
   }

   private int getTotalItemsInContainer(Container container, Item item) {
      int total = 0;

      for (int i = 0; i < container.getContainerSize(); i++) {
         ItemStack stack = container.getItem(i);
         if (stack.getItem() == item) {
            total += stack.getCount();
         }
      }

      return total;
   }

   private int extractItemsFromContainer(Container container, Item item, int amount) {
      int extracted = 0;

      for (int i = 0; i < container.getContainerSize() && extracted < amount; i++) {
         ItemStack stack = container.getItem(i);
         if (stack.getItem() == item) {
            int toExtract = Math.min(amount - extracted, stack.getCount());
            stack.shrink(toExtract);
            container.setItem(i, stack);
            extracted += toExtract;
         }
      }

      return extracted;
   }

   private void storeItemInContainer(Container container, ItemStack itemStack) {
      for (int i = 0; i < container.getContainerSize() && !itemStack.isEmpty(); i++) {
         ItemStack existingStack = container.getItem(i);
         if (!existingStack.isEmpty() && existingStack.getItem() == itemStack.getItem()) {
            int canAdd = existingStack.getMaxStackSize() - existingStack.getCount();
            if (canAdd > 0) {
               int toAdd = Math.min(canAdd, itemStack.getCount());
               existingStack.grow(toAdd);
               itemStack.shrink(toAdd);
               container.setItem(i, existingStack);
            }
         }
      }

      for (int i = 0; i < container.getContainerSize() && !itemStack.isEmpty(); i++) {
         if (container.getItem(i).isEmpty()) {
            container.setItem(i, itemStack.copy());
            itemStack.setCount(0);
            break;
         }
      }
   }

   private enum TaskState {
      SEARCHING,
      GOING_TO_SMOKER,
      AT_SMOKER_WAITING,
      GOING_TO_CHEST,
      AT_CHEST_WORKING,
      RETURNING_TO_SMOKER,
      RETURNING_EXCESS,
      WORKING_AT_SMOKER;
   }
}
