package me.rogue_one.useful_ribbits.client.gui.preview;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.lang.reflect.Field;

/**
 * A per-slot clone of vanilla's {@code GuiEntityRenderer}. Each instance is registered for a distinct
 * {@link RibbitPreviewState} subclass, so each owns its own GPU texture — see {@link RibbitPreviewState}
 * for why the shared vanilla renderer collapses all bed previews to the last one on Forge.
 *
 * <p>{@link #getTranslateY} and {@link #renderToTexture} reproduce {@code GuiEntityRenderer} exactly, so
 * previews render pixel-identically to the vanilla path.
 */
public class RibbitPreviewPipRenderer extends PictureInPictureRenderer<RibbitPreviewState> {
   private final Class<? extends RibbitPreviewState> stateClass;
   private final EntityRenderDispatcher dispatcher;

   public RibbitPreviewPipRenderer(Class<? extends RibbitPreviewState> stateClass, EntityRenderDispatcher dispatcher) {
      super();
      this.stateClass = stateClass;
      this.dispatcher = dispatcher;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Class<RibbitPreviewState> getRenderStateClass() {
      // Keyed by the concrete subclass (Chef/Miner/Farmer) so pip.getClass() dispatches to the right
      // instance; the generic is erased so the cast is safe.
      return (Class<RibbitPreviewState>) (Class<?>) this.stateClass;
   }

   @Override
   protected String getTextureLabel() {
      return "useful_ribbits:" + this.stateClass.getSimpleName();
   }

   @Override
   protected float getTranslateY(int height, int guiScale) {
      return height / 2.0F;
   }

   @Override
   protected void renderToTexture(RibbitPreviewState state, PoseStack poseStack, SubmitNodeCollector collector) {
      Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
      var translation = state.translation();
      poseStack.translate(translation.x(), translation.y(), translation.z());
      poseStack.mulPose(state.rotation());
      Quaternionfc override = state.overrideCameraAngle();
      CameraRenderState camera = new CameraRenderState();
      if (override != null) {
         camera.orientation = override.conjugate(new Quaternionf()).rotateY((float) Math.PI);
      }
      this.dispatcher.submit(state.renderState(), camera, 0.0D, 0.0D, 0.0D, poseStack, collector);
   }

   // ---- submission (used by the Forge preview path) ------------------------------------------------

   private static Field guiRenderStateField;

   /**
    * Mirror of {@code InventoryScreen.extractEntityInInventoryFollowsMouse} that submits a slot-specific
    * {@link RibbitPreviewState} instead of the shared {@code GuiEntityRenderState}. {@code slot}: 0=chef,
    * 1=miner, 2=farmer.
    */
   public static void submit(GuiGraphicsExtractor guiGraphics, int slot, int x0, int y0, int x1, int y1,
                             int scale, float yOff, float mouseX, float mouseY, LivingEntity entity) {
      EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      EntityRenderState renderState = dispatcher.extractEntity(entity, 1.0F);
      renderState.shadowPieces.clear();
      renderState.outlineColor = 0;

      float angleX = (float) Math.atan(((x0 + x1) / 2.0F - mouseX) / 40.0F);
      float angleY = (float) Math.atan(((y0 + y1) / 2.0F - mouseY) / 40.0F);
      Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf cameraAngle = new Quaternionf().rotateX(angleY * 20.0F * ((float) Math.PI / 180.0F));
      rotation.mul(cameraAngle);

      if (renderState instanceof LivingEntityRenderState living) {
         living.bodyRot = 180.0F + angleX * 20.0F;
         living.yRot = angleX * 20.0F;
         living.xRot = living.pose != Pose.FALL_FLYING ? -angleY * 20.0F : 0.0F;
         living.boundingBoxWidth = living.boundingBoxWidth / living.scale;
         living.boundingBoxHeight = living.boundingBoxHeight / living.scale;
         living.scale = 1.0F;
      }

      Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F + yOff, 0.0F);

      RibbitPreviewState state = switch (slot) {
         case 0 -> new ChefPreviewState(renderState, translation, rotation, cameraAngle, x0, y0, x1, y1, scale, null);
         case 1 -> new MinerPreviewState(renderState, translation, rotation, cameraAngle, x0, y0, x1, y1, scale, null);
         default -> new FarmerPreviewState(renderState, translation, rotation, cameraAngle, x0, y0, x1, y1, scale, null);
      };

      addToRenderState(guiGraphics, state);
   }

   private static void addToRenderState(GuiGraphicsExtractor guiGraphics, PictureInPictureRenderState pip) {
      try {
         if (guiRenderStateField == null) {
            // Minecraft ships unobfuscated on every loader here, so the field name is stable.
            Field field = GuiGraphicsExtractor.class.getDeclaredField("guiRenderState");
            field.setAccessible(true);
            guiRenderStateField = field;
         }
         GuiRenderState renderState = (GuiRenderState) guiRenderStateField.get(guiGraphics);
         renderState.addPicturesInPictureState(pip);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to submit ribbit bed preview", e);
      }
   }
}
