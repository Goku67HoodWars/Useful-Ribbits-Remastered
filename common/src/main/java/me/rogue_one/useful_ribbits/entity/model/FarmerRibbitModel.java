package me.rogue_one.useful_ribbits.entity.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.entity.FarmerRibbitEntity;
import net.minecraft.resources.Identifier;

public class FarmerRibbitModel extends GeoModel<FarmerRibbitEntity> {
   private static final Identifier MODEL = Identifier.fromNamespaceAndPath("useful_ribbits", "geo/farmer_ribbit");
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("useful_ribbits", "farmer_ribbit");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("useful_ribbits", "textures/entities/farmer_ribbit.png");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(FarmerRibbitEntity animatable) {
      return ANIMATION;
   }
}
