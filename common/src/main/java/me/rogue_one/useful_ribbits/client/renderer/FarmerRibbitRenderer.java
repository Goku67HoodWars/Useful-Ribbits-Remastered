package me.rogue_one.useful_ribbits.client.renderer;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.entity.FarmerRibbitEntity;
import me.rogue_one.useful_ribbits.entity.model.FarmerRibbitModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class FarmerRibbitRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<FarmerRibbitEntity, R> {
   public FarmerRibbitRenderer(EntityRendererProvider.Context context) {
      super(context, new FarmerRibbitModel());
      this.shadowRadius = 0.0F;
   }
}
