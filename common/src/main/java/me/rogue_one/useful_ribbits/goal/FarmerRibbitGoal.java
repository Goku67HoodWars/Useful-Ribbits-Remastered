package me.rogue_one.useful_ribbits.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FarmerRibbitGoal extends Goal {
   private final Mob mob;
   private final double speed;
   private final int searchRange;
   private FarmerRibbitGoal.FarmState currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
   private BlockPos targetChest = null;
   private List<BlockPos> farmlands = new ArrayList<>();
   private List<BlockPos> plantableSpots = new ArrayList<>();
   private List<BlockPos> harvestableSpots = new ArrayList<>();
   private BlockPos currentTarget = null;
   private int tickCounter = 0;
   private int searchCooldown = 0;
   private int stuckTicks = 0;
   private FarmerRibbitGoal.FarmState lastMoveState = null;
   private int waitingTicks = 0;
   private int seedCount = 0;
   private int wheatCount = 0;

   public FarmerRibbitGoal(Mob mob, double speed, int searchRange) {
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

      if (this.targetChest == null) {
         if (this.searchCooldown > 0) {
            this.searchCooldown--;
            return false;
         }
         this.searchCooldown = 20;
         this.targetChest = this.findNearestRibbitChestWithSeeds();
         if (this.targetChest != null) {
            this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
            this.analyzeFarmArea();
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean canContinueToUse() {
      return this.isDaytime() && this.targetChest != null;
   }

   @Override
   public void start() {
      this.tickCounter = 0;
      this.waitingTicks = 0;
      if (this.targetChest != null) {
         this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
         this.analyzeFarmArea();
      }
   }

   @Override
   public void stop() {
      this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
      this.targetChest = null;
      this.currentTarget = null;
      this.farmlands.clear();
      this.plantableSpots.clear();
      this.harvestableSpots.clear();
      this.mob.getNavigation().stop();
   }

   @Override
   public void tick() {
      if (!this.isDaytime()) {
         this.stop();
      } else {
         this.tickCounter++;
         if (this.isStuckState(this.currentState)) {
            if (this.currentState != this.lastMoveState) {
               this.lastMoveState = this.currentState;
               this.stuckTicks = 0;
            } else if (++this.stuckTicks > 300) {
               // Wedged for ~15s trying to reach a spot/chest: skip it and re-plan.
               this.stuckTicks = 0;
               this.mob.getNavigation().stop();
               if (this.currentState == FarmerRibbitGoal.FarmState.GOING_TO_PLANT
                     || this.currentState == FarmerRibbitGoal.FarmState.GOING_TO_HARVEST) {
                  if (this.currentTarget != null) {
                     this.plantableSpots.remove(this.currentTarget);
                     this.harvestableSpots.remove(this.currentTarget);
                  }
                  this.currentTarget = null;
                  this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
               } else {
                  this.targetChest = null;
               }
               return;
            }
         } else {
            this.stuckTicks = 0;
            this.lastMoveState = null;
         }
         switch (this.currentState) {
            case GOING_TO_CHEST:
               this.handleGoingToChest();
               break;
            case AT_CHEST_TAKING_SEEDS:
               this.handleTakingSeeds();
               break;
            case GOING_TO_PLANT:
               this.handleGoingToPlant();
               break;
            case PLANTING:
               this.handlePlanting();
               break;
            case GOING_TO_HARVEST:
               this.handleGoingToHarvest();
               break;
            case HARVESTING:
               this.handleHarvesting();
               break;
            case DEPOSITING:
               this.handleDepositing();
               break;
            case WAITING_AT_CHEST:
               this.handleWaitingAtChest();
         }
      }
   }

   private void handleGoingToChest() {
      double distance = this.mob.distanceToSqr(this.targetChest.getX() + 0.5, this.targetChest.getY(), this.targetChest.getZ() + 0.5);
      if (distance < 1.5) {
         this.mob.getNavigation().stop();
         this.currentState = FarmerRibbitGoal.FarmState.AT_CHEST_TAKING_SEEDS;
      } else {
         this.mob.getNavigation().moveTo(this.targetChest.getX() + 0.5, this.targetChest.getY(), this.targetChest.getZ() + 0.5, this.speed);
      }
   }

   private void handleTakingSeeds() {
      this.updateTaskLists();
      int seedsNeeded = this.plantableSpots.size();
      int seedsToTake = Math.min(seedsNeeded, 64 - this.seedCount);
      if (seedsToTake > 0) {
         this.takeSeeds(seedsToTake);
      }

      if (!this.harvestableSpots.isEmpty()) {
         this.currentTarget = this.harvestableSpots.get(0);
         this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_HARVEST;
      } else if (!this.plantableSpots.isEmpty() && this.seedCount > 0) {
         this.currentTarget = this.getClosestPlantableSpot();
         this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_PLANT;
      } else {
         this.currentState = FarmerRibbitGoal.FarmState.WAITING_AT_CHEST;
         this.waitingTicks = 0;
      }
   }

   private void handleGoingToPlant() {
      if (this.currentTarget == null) {
         this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
      } else {
         double targetX = this.currentTarget.getX() + 0.5;
         double targetY = this.currentTarget.getY() + 1;
         double targetZ = this.currentTarget.getZ() + 0.5;
         double distance = this.mob.distanceToSqr(targetX, targetY, targetZ);
         if (Math.sqrt(distance) < 1.5) {
            this.currentState = FarmerRibbitGoal.FarmState.PLANTING;
            this.mob.getNavigation().stop();
         } else if (this.mob.getNavigation().isDone() || !this.mob.getNavigation().isInProgress()) {
            boolean pathSet = this.mob.getNavigation().moveTo(targetX, targetY, targetZ, this.speed);
            if (!pathSet) {
               this.currentState = FarmerRibbitGoal.FarmState.PLANTING;
            }
         }
      }
   }

   private void handlePlanting() {
      if (this.currentTarget != null && this.seedCount > 0) {
         BlockPos farmlandPos = this.currentTarget.below();
         BlockState farmlandState = this.mob.level().getBlockState(farmlandPos);
         BlockState currentBlockState = this.mob.level().getBlockState(this.currentTarget);
         if (this.plantSeed(this.currentTarget)) {
            this.seedCount--;
            this.plantableSpots.remove(this.currentTarget);
            if (!this.plantableSpots.isEmpty() && this.seedCount > 0) {
               this.currentTarget = this.getClosestPlantableSpot();
               this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_PLANT;
            } else {
               this.currentTarget = null;
               this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
            }
         } else if (farmlandState.getBlock() instanceof FarmlandBlock && currentBlockState.isAir()) {
            this.mob.level().setBlock(this.currentTarget, Blocks.WHEAT.defaultBlockState(), 3);
            this.seedCount--;
            this.plantableSpots.remove(this.currentTarget);
            if (!this.plantableSpots.isEmpty() && this.seedCount > 0) {
               this.currentTarget = this.getClosestPlantableSpot();
               this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_PLANT;
            } else {
               this.currentTarget = null;
               this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
            }
         } else {
            this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
         }
      } else {
         this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
      }
   }

   private void handleGoingToHarvest() {
      if (this.currentTarget == null) {
         this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
      } else {
         double targetX = this.currentTarget.getX() + 0.5;
         double targetY = this.currentTarget.getY();
         double targetZ = this.currentTarget.getZ() + 0.5;
         double distance = this.mob.distanceToSqr(targetX, targetY, targetZ);
         if (Math.sqrt(distance) < 0.25) {
            this.currentState = FarmerRibbitGoal.FarmState.HARVESTING;
            this.mob.getNavigation().stop();
         } else {
            this.mob.getNavigation().moveTo(targetX, targetY + 1.0, targetZ, this.speed * 1.2);
            if (this.mob.getNavigation().isStuck()) {
               this.mob.snapTo(targetX, targetY, targetZ);
               this.currentState = FarmerRibbitGoal.FarmState.HARVESTING;
            }
         }
      }
   }

   private void handleHarvesting() {
      if (this.currentTarget == null) {
         this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
      } else {
         double targetX = this.currentTarget.getX() + 0.5;
         double targetY = this.currentTarget.getY() - 0.1;
         double targetZ = this.currentTarget.getZ() + 0.5;
         double distance = this.mob.distanceToSqr(targetX, targetY, targetZ);
         if (distance > 0.1) {
            this.mob.snapTo(targetX, targetY, targetZ);
         } else if (this.harvestCrop(this.currentTarget)) {
            this.harvestableSpots.remove(this.currentTarget);
            if (this.seedCount > 0 && this.plantSeed(this.currentTarget)) {
               this.seedCount--;
            }

            this.currentTarget = null;
            this.currentState = FarmerRibbitGoal.FarmState.DEPOSITING;
         } else {
            this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_CHEST;
         }
      }
   }

   private void handleDepositing() {
      double distance = this.mob.distanceToSqr(this.targetChest.getX() + 0.5, this.targetChest.getY(), this.targetChest.getZ() + 0.5);
      if (distance < 1.5) {
         this.depositItems();
         this.currentState = FarmerRibbitGoal.FarmState.WAITING_AT_CHEST;
         this.waitingTicks = 0;
      } else {
         this.mob.getNavigation().moveTo(this.targetChest.getX() + 0.5, this.targetChest.getY(), this.targetChest.getZ() + 0.5, this.speed);
      }
   }

   private void handleWaitingAtChest() {
      this.waitingTicks++;
      if (this.waitingTicks >= 40) {
         this.waitingTicks = 0;
         this.updateTaskLists();
         if (!this.harvestableSpots.isEmpty()) {
            this.currentTarget = this.harvestableSpots.get(0);
            this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_HARVEST;
         } else if (!this.plantableSpots.isEmpty()) {
            // Bare farmland to replant. Use seeds we are already carrying; only walk to the chest
            // for more when our hands are empty. (The old code only checked the chest, so a farmer
            // holding seeds over a drained chest would sit idle forever instead of replanting.)
            if (this.seedCount > 0) {
               this.currentTarget = this.getClosestPlantableSpot();
               this.currentState = FarmerRibbitGoal.FarmState.GOING_TO_PLANT;
            } else if (this.chestHasSeeds(this.targetChest)) {
               this.currentState = FarmerRibbitGoal.FarmState.AT_CHEST_TAKING_SEEDS;
            }
         }
      }

      double distance = this.mob.distanceToSqr(this.targetChest.getX() + 0.5, this.targetChest.getY(), this.targetChest.getZ() + 0.5);
      if (distance > 2.0) {
         this.mob.getNavigation().moveTo(this.targetChest.getX() + 0.5, this.targetChest.getY(), this.targetChest.getZ() + 0.5, this.speed);
      }
   }

   private BlockPos getClosestPlantableSpot() {
      if (this.plantableSpots.isEmpty()) {
         return null;
      }

      this.plantableSpots.sort((pos1, pos2) -> Double.compare(this.targetChest.distSqr(pos1), this.targetChest.distSqr(pos2)));
      return this.plantableSpots.get(0);
   }

   private boolean isStuckState(FarmerRibbitGoal.FarmState s) {
      return s == FarmerRibbitGoal.FarmState.GOING_TO_CHEST
          || s == FarmerRibbitGoal.FarmState.GOING_TO_PLANT
          || s == FarmerRibbitGoal.FarmState.GOING_TO_HARVEST
          || s == FarmerRibbitGoal.FarmState.DEPOSITING;
   }

   private boolean isDaytime() {
      long time = this.mob.level().getOverworldClockTime() % 24000L;
      return time >= 0L && time < 13000L;
   }

   private BlockPos findNearestRibbitChestWithSeeds() {
      BlockPos mobPos = this.mob.blockPosition();

      for (int x = -this.searchRange; x <= this.searchRange; x++) {
         for (int y = -8; y <= 8; y++) {
            for (int z = -this.searchRange; z <= this.searchRange; z++) {
               BlockPos pos = mobPos.offset(x, y, z);
               BlockState state = this.mob.level().getBlockState(pos);
               if (state.is(UsefulRibbitsModBlocks.RIBBIT_CHEST) && this.chestHasSeeds(pos)) {
                  return pos;
               }
            }
         }
      }

      return null;
   }

   private boolean chestHasSeeds(BlockPos pos) {
      if (this.mob.level().getBlockEntity(pos) instanceof Container container) {
         for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).is(Items.WHEAT_SEEDS)) {
               return true;
            }
         }
      }

      return false;
   }

   private void analyzeFarmArea() {
      this.farmlands.clear();

      for (int x = -32; x <= 32; x++) {
         for (int y = -3; y <= 3; y++) {
            for (int z = -32; z <= 32; z++) {
               BlockPos pos = this.targetChest.offset(x, y, z);
               BlockState state = this.mob.level().getBlockState(pos);
               if (state.getBlock() instanceof FarmlandBlock) {
                  this.farmlands.add(pos);
               }
            }
         }
      }
   }

   private void updateTaskLists() {
      this.plantableSpots.clear();
      this.harvestableSpots.clear();

      for (BlockPos farmPos : this.farmlands) {
         BlockPos cropPos = farmPos.above();
         BlockState cropState = this.mob.level().getBlockState(cropPos);
         if (cropState.isAir()) {
            this.plantableSpots.add(cropPos);
         } else if (cropState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(cropState)) {
            this.harvestableSpots.add(cropPos);
         }
      }
   }

   private void takeSeeds(int amount) {
      if (this.mob.level().getBlockEntity(this.targetChest) instanceof Container container) {
         int taken = 0;

         for (int i = 0; i < container.getContainerSize() && taken < amount; i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(Items.WHEAT_SEEDS)) {
               int toTake = Math.min(stack.getCount(), amount - taken);
               stack.shrink(toTake);
               container.setItem(i, stack);
               taken += toTake;
               this.seedCount += toTake;
            }
         }
      }
   }

   private boolean plantSeed(BlockPos pos) {
      BlockState farmland = this.mob.level().getBlockState(pos.below());
      if (farmland.getBlock() instanceof FarmlandBlock && this.mob.level().getBlockState(pos).isAir()) {
         this.mob.level().setBlock(pos, Blocks.WHEAT.defaultBlockState(), 3);
         return true;
      } else {
         return false;
      }
   }

   private boolean harvestCrop(BlockPos pos) {
      BlockState state = this.mob.level().getBlockState(pos);
      if (state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state)) {
         int harvestedWheat = 1 + this.mob.level().getRandom().nextInt(3);
         int harvestedSeeds = this.mob.level().getRandom().nextInt(4);
         this.wheatCount += harvestedWheat;
         this.seedCount += harvestedSeeds;
         if (this.seedCount > 64) {
            this.seedCount = 64;
         }

         this.mob.level().destroyBlock(pos, false, this.mob, 512);
         return true;
      } else {
         return false;
      }
   }

   private void depositItems() {
      if (this.mob.level().getBlockEntity(this.targetChest) instanceof Container container) {
         if (this.wheatCount > 0) {
            ItemStack wheatStack = new ItemStack(Items.WHEAT, this.wheatCount);

            for (int i = 0; i < container.getContainerSize() && !wheatStack.isEmpty(); i++) {
               wheatStack = insertInto(container, i, wheatStack);
            }

            this.wheatCount = wheatStack.getCount();
         }

         int seedsToDeposit = Math.max(0, this.seedCount - 16);
         if (seedsToDeposit > 0) {
            ItemStack seedStack = new ItemStack(Items.WHEAT_SEEDS, seedsToDeposit);
            int before = seedStack.getCount();

            for (int i = 0; i < container.getContainerSize() && !seedStack.isEmpty(); i++) {
               seedStack = insertInto(container, i, seedStack);
            }

            this.seedCount -= before - seedStack.getCount();
         }
      }
   }

   private static ItemStack insertInto(Container container, int slot, ItemStack stack) {
      ItemStack existing = container.getItem(slot);
      if (existing.isEmpty()) {
         int toPlace = Math.min(stack.getCount(), container.getMaxStackSize());
         ItemStack placed = stack.copy();
         placed.setCount(toPlace);
         container.setItem(slot, placed);
         stack.shrink(toPlace);
      } else if (existing.getItem() == stack.getItem()) {
         int canAdd = Math.min(existing.getMaxStackSize(), container.getMaxStackSize()) - existing.getCount();
         if (canAdd > 0) {
            int toAdd = Math.min(canAdd, stack.getCount());
            existing.grow(toAdd);
            container.setItem(slot, existing);
            stack.shrink(toAdd);
         }
      }

      return stack;
   }

   private enum FarmState {
      GOING_TO_CHEST,
      AT_CHEST_TAKING_SEEDS,
      PLANTING,
      GOING_TO_PLANT,
      HARVESTING,
      GOING_TO_HARVEST,
      DEPOSITING,
      WAITING_AT_CHEST;
   }
}
