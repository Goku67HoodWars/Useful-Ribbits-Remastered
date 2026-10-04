package me.rogue_one.useful_ribbits.client.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.block.entity.RibbitChestBlockEntity;
import net.minecraft.resources.Identifier;

/** GeckoLib model for the animated Ribbit Chest (converted from the original Blockbench model). */
public class RibbitChestModel extends GeoModel<RibbitChestBlockEntity> {
   private static final Identifier MODEL = Identifier.fromNamespaceAndPath("useful_ribbits", "geo/ribbit_chest");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("useful_ribbits", "textures/block/ribbit_chest.png");
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("useful_ribbits", "ribbit_chest");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(RibbitChestBlockEntity animatable) {
      return ANIMATION;
   }
}
