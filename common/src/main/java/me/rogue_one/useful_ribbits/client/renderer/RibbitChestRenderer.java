package me.rogue_one.useful_ribbits.client.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import me.rogue_one.useful_ribbits.block.entity.RibbitChestBlockEntity;
import me.rogue_one.useful_ribbits.client.model.RibbitChestModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/** Renders the Ribbit Chest with GeckoLib so its lid can hinge open/closed. */
public class RibbitChestRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<RibbitChestBlockEntity, R> {
   public RibbitChestRenderer(BlockEntityRendererProvider.Context context) {
      super(context, new RibbitChestModel());
   }
}
