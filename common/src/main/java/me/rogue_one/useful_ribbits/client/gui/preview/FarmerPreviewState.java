package me.rogue_one.useful_ribbits.client.gui.preview;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

/** Distinct pip class for the Farmer bed preview so it maps to its own renderer/texture. */
public final class FarmerPreviewState extends RibbitPreviewState {
   public FarmerPreviewState(EntityRenderState renderState, Vector3fc translation, Quaternionfc rotation,
                        Quaternionfc overrideCameraAngle, int x0, int y0, int x1, int y1, float scale,
                        ScreenRectangle scissorArea) {
      super(renderState, translation, rotation, overrideCameraAngle, x0, y0, x1, y1, scale, scissorArea);
   }
}
