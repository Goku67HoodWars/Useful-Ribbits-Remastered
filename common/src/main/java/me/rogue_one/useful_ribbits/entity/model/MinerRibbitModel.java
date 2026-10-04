package me.rogue_one.useful_ribbits.entity.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.entity.MinerRibbitEntity;
import net.minecraft.resources.Identifier;

public class MinerRibbitModel extends GeoModel<MinerRibbitEntity> {
   private static final Identifier MODEL = Identifier.fromNamespaceAndPath("useful_ribbits", "geo/miner_ribbit");
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("useful_ribbits", "miner_ribbit");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("useful_ribbits", "textures/entities/miner_ribbit.png");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(MinerRibbitEntity animatable) {
      return ANIMATION;
   }
}
