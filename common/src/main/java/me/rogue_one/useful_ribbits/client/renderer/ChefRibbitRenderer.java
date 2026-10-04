package me.rogue_one.useful_ribbits.client.renderer;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.entity.ChefRibbitEntity;
import me.rogue_one.useful_ribbits.entity.model.ChefRibbitModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class ChefRibbitRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<ChefRibbitEntity, R> {
   public ChefRibbitRenderer(EntityRendererProvider.Context context) {
      super(context, new ChefRibbitModel());
      this.shadowRadius = 0.0F;
   }
}
