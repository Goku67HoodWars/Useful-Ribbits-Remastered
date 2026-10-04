package me.rogue_one.useful_ribbits.procedures;

import me.rogue_one.useful_ribbits.entity.FarmerRibbitEntity;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RibbitBedGUIRenderFarmerProcedure {
   // Cached, stable preview instance (one per client level). Recreating a fresh dummy every frame +
   // reusing the same id confused Forge's retained-mode deferred draw (all 3 previews collapsed to
   // the last one). A stable instance with a distinct positive id keeps each preview independent.
   private static FarmerRibbitEntity cached;

   public static Entity execute(LevelAccessor world) {
      if (!(world instanceof Level level)) {
         return null;
      }
      if (cached == null || cached.level() != level) {
         cached = new FarmerRibbitEntity(UsefulRibbitsModEntities.FARMER_RIBBIT, level);
         cached.setId(1003);
      }
      return cached;
   }
}
