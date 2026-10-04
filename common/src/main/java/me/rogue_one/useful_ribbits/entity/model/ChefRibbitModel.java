package me.rogue_one.useful_ribbits.entity.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.entity.ChefRibbitEntity;
import net.minecraft.resources.Identifier;

public class ChefRibbitModel extends GeoModel<ChefRibbitEntity> {
   // GeckoLib 5 resolves model keys under assets/<ns>/geckolib/models and animation keys under
   // assets/<ns>/geckolib/animations (the .geo.json / .animation.json suffix is stripped).
   private static final Identifier MODEL = Identifier.fromNamespaceAndPath("useful_ribbits", "geo/chef_ribbit");
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("useful_ribbits", "chef_ribbit");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("useful_ribbits", "textures/entities/chef_ribbit.png");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(ChefRibbitEntity animatable) {
      return ANIMATION;
   }
}
