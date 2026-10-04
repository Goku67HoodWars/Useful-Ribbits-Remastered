package me.rogue_one.useful_ribbits.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RibbitChestGUIThisGUIIsClosedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level) {
         if (!level.isClientSide()) {
            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.4F, 1.0F);
         } else {
            level.playLocalSound(x, y, z, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.4F, 1.0F, false);
         }
      }
   }
}
