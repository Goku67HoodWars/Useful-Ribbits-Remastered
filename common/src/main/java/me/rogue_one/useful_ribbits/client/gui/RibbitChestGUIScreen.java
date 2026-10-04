package me.rogue_one.useful_ribbits.client.gui;

import me.rogue_one.useful_ribbits.world.inventory.RibbitChestGUIMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RibbitChestGUIScreen extends AbstractContainerScreen<RibbitChestGUIMenu> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("useful_ribbits", "textures/screens/ribbit_chest_gui.png");

   public RibbitChestGUIScreen(RibbitChestGUIMenu container, Inventory inventory, Component text) {
      super(container, inventory, text, 176, 166);
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.blit(
         RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight
      );
   }

   @Override
   protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
      guiGraphics.text(this.font, Component.translatable("gui.useful_ribbits.ribbit_chest_gui.label_ribbit_chest"), 8, 7, -12829636, false);
   }
}
