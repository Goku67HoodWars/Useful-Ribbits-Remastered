package me.rogue_one.useful_ribbits.client.gui.preview;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

/**
 * Picture-in-picture render state for ONE bed ribbit preview.
 *
 * <p>The vanilla path ({@code InventoryScreen.extractEntityInInventoryFollowsMouse}) routes every
 * entity preview through the single shared {@code GuiEntityRenderer}, which renders each pip into its
 * ONE reused GPU texture and records a <em>deferred</em> blit of that texture. When several entity
 * previews are submitted in one frame the shared texture ends up holding only the last one, so on
 * Forge all three bed previews collapse into the last ribbit. (NeoForge/Fabric tolerate it; Forge
 * does not.)
 *
 * <p>Fix: one concrete subclass per slot. Each subclass maps to its own
 * {@link RibbitPreviewPipRenderer} instance, so every preview owns a distinct texture and the deferred
 * blits can never alias. The data mirrors {@code GuiEntityRenderState} exactly, so rendering is
 * pixel-identical to vanilla.
 */
public abstract class RibbitPreviewState implements PictureInPictureRenderState {
   private final EntityRenderState renderState;
   private final Vector3fc translation;
   private final Quaternionfc rotation;
   private final Quaternionfc overrideCameraAngle;
   private final int x0;
   private final int y0;
   private final int x1;
   private final int y1;
   private final float scale;
   private final ScreenRectangle scissorArea;
   private final ScreenRectangle bounds;

   protected RibbitPreviewState(EntityRenderState renderState, Vector3fc translation, Quaternionfc rotation,
                                Quaternionfc overrideCameraAngle, int x0, int y0, int x1, int y1, float scale,
                                ScreenRectangle scissorArea) {
      this.renderState = renderState;
      this.translation = translation;
      this.rotation = rotation;
      this.overrideCameraAngle = overrideCameraAngle;
      this.x0 = x0;
      this.y0 = y0;
      this.x1 = x1;
      this.y1 = y1;
      this.scale = scale;
      this.scissorArea = scissorArea;
      this.bounds = PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea);
   }

   public EntityRenderState renderState() {
      return this.renderState;
   }

   public Vector3fc translation() {
      return this.translation;
   }

   public Quaternionfc rotation() {
      return this.rotation;
   }

   public Quaternionfc overrideCameraAngle() {
      return this.overrideCameraAngle;
   }

   @Override
   public int x0() {
      return this.x0;
   }

   @Override
   public int y0() {
      return this.y0;
   }

   @Override
   public int x1() {
      return this.x1;
   }

   @Override
   public int y1() {
      return this.y1;
   }

   @Override
   public float scale() {
      return this.scale;
   }

   @Override
   public ScreenRectangle scissorArea() {
      return this.scissorArea;
   }

   @Override
   public ScreenRectangle bounds() {
      return this.bounds;
   }
}
