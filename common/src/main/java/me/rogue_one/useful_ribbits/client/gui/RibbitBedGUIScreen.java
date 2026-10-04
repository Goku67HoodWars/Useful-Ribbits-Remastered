package me.rogue_one.useful_ribbits.client.gui;

import me.rogue_one.useful_ribbits.network.RibbitBedGUIButtonMessage;
import me.rogue_one.useful_ribbits.procedures.RibbitBedGUIRenderChefProcedure;
import me.rogue_one.useful_ribbits.procedures.RibbitBedGUIRenderFarmerProcedure;
import me.rogue_one.useful_ribbits.procedures.RibbitBedGUIRenderMinerProcedure;
import me.rogue_one.useful_ribbits.world.inventory.RibbitBedGUIMenu;
import me.rogue_one.useful_ribbits.platform.PlatformHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

public class RibbitBedGUIScreen extends AbstractContainerScreen<RibbitBedGUIMenu> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("useful_ribbits", "textures/screens/ribbit_bed_gui.png");
   private static final Identifier IRON_INGOT = Identifier.fromNamespaceAndPath("useful_ribbits", "textures/screens/iron_ingot.png");
   private final Level world;

   public RibbitBedGUIScreen(RibbitBedGUIMenu container, Inventory inventory, Component text) {
      super(container, inventory, text, 176, 200);
      this.world = container.world;
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.blit(
         RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight
      );
      guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IRON_INGOT, this.leftPos + 79, this.topPos + 9, 0.0F, 0.0F, 16, 16, 16, 16);

      if (RibbitBedGUIRenderChefProcedure.execute(this.world) instanceof LivingEntity chef) {
         this.preview(guiGraphics, this.leftPos + 11, this.topPos + 24, this.leftPos + 27, this.topPos + 50, mouseX, mouseY, chef, 0);
      }

      if (RibbitBedGUIRenderMinerProcedure.execute(this.world) instanceof LivingEntity miner) {
         this.preview(guiGraphics, this.leftPos + 11, this.topPos + 52, this.leftPos + 27, this.topPos + 78, mouseX, mouseY, miner, 1);
      }

      if (RibbitBedGUIRenderFarmerProcedure.execute(this.world) instanceof LivingEntity farmer) {
         this.preview(guiGraphics, this.leftPos + 11, this.topPos + 80, this.leftPos + 27, this.topPos + 106, mouseX, mouseY, farmer, 2);
      }
   }

   // Forge collapses all three previews to the last one when they share vanilla's single GuiEntityRenderer
   // (deferred blit of one reused texture). There, route each slot through its own picture-in-picture
   // renderer; elsewhere the vanilla helper works, so keep it.
   private void preview(GuiGraphicsExtractor guiGraphics, int x0, int y0, int x1, int y1, float mouseX, float mouseY, LivingEntity entity, int slot) {
      if (PlatformHelper.getPlatformService().useCustomEntityPreview()) {
         me.rogue_one.useful_ribbits.client.gui.preview.RibbitPreviewPipRenderer.submit(
            guiGraphics, slot, x0, y0, x1, y1, 25, 0.0625F, mouseX, mouseY, entity
         );
      } else {
         InventoryScreen.extractEntityInInventoryFollowsMouse(guiGraphics, x0, y0, x1, y1, 25, 0.0625F, mouseX, mouseY, entity);
      }
   }

   @Override
   protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
      // The original screen draws no title/inventory labels.
   }

   @Override
   protected void init() {
      super.init();
      // Button ids: 0 = miner, 1 = chef, 2 = farmer (matches the server-side handler).
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.useful_ribbits.ribbit_bed_gui.button_empty"), e -> PlatformHelper.sendToServer(new RibbitBedGUIButtonMessage(1)))
            .bounds(this.leftPos + 31, this.topPos + 30, 30, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.useful_ribbits.ribbit_bed_gui.button_empty1"), e -> PlatformHelper.sendToServer(new RibbitBedGUIButtonMessage(0)))
            .bounds(this.leftPos + 31, this.topPos + 58, 30, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.useful_ribbits.ribbit_bed_gui.button_empty2"), e -> PlatformHelper.sendToServer(new RibbitBedGUIButtonMessage(2)))
            .bounds(this.leftPos + 32, this.topPos + 86, 30, 20)
            .build()
      );
   }
}
