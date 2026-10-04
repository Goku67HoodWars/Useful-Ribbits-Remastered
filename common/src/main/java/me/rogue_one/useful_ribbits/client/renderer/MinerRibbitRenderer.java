package me.rogue_one.useful_ribbits.client.renderer;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.entity.MinerRibbitEntity;
import me.rogue_one.useful_ribbits.entity.model.MinerRibbitModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class MinerRibbitRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<MinerRibbitEntity, R> {
   public MinerRibbitRenderer(EntityRendererProvider.Context context) {
      super(context, new MinerRibbitModel());
      this.shadowRadius = 0.0F;
   }
}
