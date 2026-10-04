package me.rogue_one.useful_ribbits.block.entity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import java.util.stream.IntStream;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlockEntities;
import me.rogue_one.useful_ribbits.world.inventory.RibbitChestGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class RibbitChestBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, GeoBlockEntity {
   private NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("open");
   private static final RawAnimation CLOSE = RawAnimation.begin().thenPlayAndHold("close");

   public RibbitChestBlockEntity(BlockPos position, BlockState state) {
      super(UsefulRibbitsModBlockEntities.RIBBIT_CHEST_SUP.get(), position, state);
   }

   @Override
   protected void loadAdditional(ValueInput input) {
      super.loadAdditional(input);
      this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(input, this.stacks);
   }

   @Override
   protected void saveAdditional(ValueOutput output) {
      super.saveAdditional(output);
      ContainerHelper.saveAllItems(output, this.stacks);
   }

   @Override
   public int getContainerSize() {
      return 27;
   }

   @Override
   protected Component getDefaultName() {
      return Component.literal("Ribbit Chest");
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
      return new RibbitChestGUIMenu(id, inventory, this);
   }

   @Override
   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return true;
   }

   @Override
   public int[] getSlotsForFace(Direction side) {
      return IntStream.range(0, this.getContainerSize()).toArray();
   }

   @Override
   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
      return this.canPlaceItem(index, stack);
   }

   @Override
   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return true;
   }

   @Override
   public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
      // Idle = whatever pose we are in (closed by default); the lid swings via server-triggered anims.
      controllers.add(new AnimationController<>("lid", 5, state -> PlayState.STOP)
         .triggerableAnim("open", OPEN)
         .triggerableAnim("close", CLOSE));
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
