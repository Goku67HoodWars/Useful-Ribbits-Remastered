package me.rogue_one.useful_ribbits.world.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModMenus;
import me.rogue_one.useful_ribbits.procedures.RibbitChestGUIThisGUIIsClosedProcedure;
import me.rogue_one.useful_ribbits.procedures.RibbitChestGUIThisGUIIsOpenedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RibbitChestGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
   private static final int CONTAINER_SIZE = 27;
   public final Level world;
   public final Player entity;
   public int x;
   public int y;
   public int z;
   private final Container container;
   private final Map<Integer, Slot> customSlots = new HashMap<>();
   private boolean hasPos = false;

   /** Client-side constructor: a synced dummy container of the right size. */
   public RibbitChestGUIMenu(int id, Inventory inv) {
      this(id, inv, new SimpleContainer(CONTAINER_SIZE));
   }

   /** Server-side constructor: bound to the block entity's real container. */
   public RibbitChestGUIMenu(int id, Inventory inv, Container container) {
      super(UsefulRibbitsModMenus.RIBBIT_CHEST_GUI.get(), id);
      checkContainerSize(container, CONTAINER_SIZE);
      this.entity = inv.player;
      this.world = inv.player.level();
      this.container = container;
      if (container instanceof BlockEntity be) {
         BlockPos pos = be.getBlockPos();
         this.x = pos.getX();
         this.y = pos.getY();
         this.z = pos.getZ();
         this.hasPos = true;
      }

      int slot = 0;
      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.customSlots.put(slot, this.addSlot(new Slot(container, slot, 8 + col * 18, 20 + row * 18)));
            slot++;
         }
      }

      for (int si = 0; si < 3; si++) {
         for (int sj = 0; sj < 9; sj++) {
            this.addSlot(new Slot(inv, sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
         }
      }

      for (int si = 0; si < 9; si++) {
         this.addSlot(new Slot(inv, si, 8 + si * 18, 142));
      }

      if (this.hasPos) {
         RibbitChestGUIThisGUIIsOpenedProcedure.execute(this.world, this.x, this.y, this.z);
         if (this.container instanceof me.rogue_one.useful_ribbits.block.entity.RibbitChestBlockEntity be) {
            be.triggerAnim("lid", "open");
         }
      }
   }

   @Override
   public boolean stillValid(Player player) {
      return this.container.stillValid(player);
   }

   @Override
   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack result = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack stack = slot.getItem();
         result = stack.copy();
         if (index < CONTAINER_SIZE) {
            if (!this.moveItemStackTo(stack, CONTAINER_SIZE, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(stack, 0, CONTAINER_SIZE, false)) {
            return ItemStack.EMPTY;
         }

         if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return result;
   }

   @Override
   public void removed(Player player) {
      super.removed(player);
      if (this.hasPos) {
         RibbitChestGUIThisGUIIsClosedProcedure.execute(this.world, this.x, this.y, this.z);
         if (this.container instanceof me.rogue_one.useful_ribbits.block.entity.RibbitChestBlockEntity be) {
            be.triggerAnim("lid", "close");
         }
      }
   }

   @Override
   public Map<Integer, Slot> get() {
      return this.customSlots;
   }
}
