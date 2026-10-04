package me.rogue_one.useful_ribbits.procedures;

import java.util.Map;
import java.util.function.Supplier;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;

public class RBGUIChefBtnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         ItemStack slotStack = ItemStack.EMPTY;
         if (entity instanceof Player plr && plr.containerMenu instanceof Supplier<?> splr && splr.get() instanceof Map<?, ?> slots
            && slots.get(0) instanceof Slot slot) {
            slotStack = slot.getItem();
         }

         if (Items.IRON_INGOT == slotStack.getItem() && world instanceof ServerLevel level) {
            Entity entityToSpawn = UsefulRibbitsModEntities.CHEF_RIBBIT.spawn(level, BlockPos.containing(x, y + 1.0, z), EntitySpawnReason.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
               // Consume the iron only when a ribbit actually spawned (a cancelled spawn keeps it).
               if (entity instanceof Player player && player.containerMenu instanceof Supplier<?> current && current.get() instanceof Map<?, ?> slots
                  && slots.get(0) instanceof Slot slot) {
                  slot.remove(1);
                  player.containerMenu.broadcastChanges();
               }
            }
         }
      }
   }
}
