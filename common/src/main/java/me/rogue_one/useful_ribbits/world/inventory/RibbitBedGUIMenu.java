package me.rogue_one.useful_ribbits.world.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RibbitBedGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
   public final Level world;
   public final Player entity;
   public int x;
   public int y;
   public int z;
   private final Container container = new SimpleContainer(1);
   private final Map<Integer, Slot> customSlots = new HashMap<>();

   /** Client-side constructor: the bed position is unknown client-side (read server-side instead). */
   public RibbitBedGUIMenu(int id, Inventory inv) {
      this(id, inv, null);
   }

   public RibbitBedGUIMenu(int id, Inventory inv, BlockPos pos) {
      super(UsefulRibbitsModMenus.RIBBIT_BED_GUI.get(), id);
      this.entity = inv.player;
      this.world = inv.player.level();
      if (pos != null) {
         this.x = pos.getX();
         this.y = pos.getY();
         this.z = pos.getZ();
      }

      this.customSlots.put(0, this.addSlot(new Slot(this.container, 0, 79, 9)));

      for (int si = 0; si < 3; si++) {
         for (int sj = 0; sj < 9; sj++) {
            this.addSlot(new Slot(inv, sj + (si + 1) * 9, 8 + sj * 18, 118 + si * 18));
         }
      }

      for (int si = 0; si < 9; si++) {
         this.addSlot(new Slot(inv, si, 8 + si * 18, 176));
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
         if (index < 1) {
            if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(stack, 0, 1, false)) {
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
      if (player instanceof ServerPlayer serverPlayer) {
         for (int i = 0; i < this.container.getContainerSize(); i++) {
            ItemStack stack = this.container.removeItemNoUpdate(i);
            if (!stack.isEmpty()) {
               serverPlayer.getInventory().placeItemBackInInventory(stack, Prediction.SERVER_ONLY);
            }
         }
      }
   }

   @Override
   public Map<Integer, Slot> get() {
      return this.customSlots;
   }
}
