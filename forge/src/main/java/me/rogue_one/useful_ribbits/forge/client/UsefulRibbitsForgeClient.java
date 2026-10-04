package me.rogue_one.useful_ribbits.forge.client;

import me.rogue_one.useful_ribbits.client.gui.RibbitBedGUIScreen;
import me.rogue_one.useful_ribbits.client.gui.RibbitChestGUIScreen;
import me.rogue_one.useful_ribbits.client.renderer.ChefRibbitRenderer;
import me.rogue_one.useful_ribbits.client.renderer.FarmerRibbitRenderer;
import me.rogue_one.useful_ribbits.client.renderer.MinerRibbitRenderer;
import me.rogue_one.useful_ribbits.client.gui.preview.ChefPreviewState;
import me.rogue_one.useful_ribbits.client.gui.preview.FarmerPreviewState;
import me.rogue_one.useful_ribbits.client.gui.preview.MinerPreviewState;
import me.rogue_one.useful_ribbits.client.gui.preview.RibbitPreviewPipRenderer;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterPictureInPictureRendererEvent;

public class UsefulRibbitsForgeClient {

    public static void init() {
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(UsefulRibbitsForgeClient::registerRenderers);
        RegisterPictureInPictureRendererEvent.BUS.addListener(UsefulRibbitsForgeClient::registerPreviewRenderers);
    }

    // One picture-in-picture renderer per bed-preview slot, each with its own GPU texture, so Forge's
    // deferred GUI blit can't collapse the three previews into the last ribbit.
    private static void registerPreviewRenderers(RegisterPictureInPictureRendererEvent event) {
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        event.register(new RibbitPreviewPipRenderer(ChefPreviewState.class, dispatcher));
        event.register(new RibbitPreviewPipRenderer(MinerPreviewState.class, dispatcher));
        event.register(new RibbitPreviewPipRenderer(FarmerPreviewState.class, dispatcher));
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // GeoEntityRenderer needs no layer definitions; GeckoLib loads the model from resources.
        event.registerEntityRenderer(UsefulRibbitsModEntities.CHEF_RIBBIT_SUP.get(), ChefRibbitRenderer::new);
        event.registerEntityRenderer(UsefulRibbitsModEntities.MINER_RIBBIT_SUP.get(), MinerRibbitRenderer::new);
        event.registerEntityRenderer(UsefulRibbitsModEntities.FARMER_RIBBIT_SUP.get(), FarmerRibbitRenderer::new);
        event.registerBlockEntityRenderer(me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlockEntities.RIBBIT_CHEST_SUP.get(), me.rogue_one.useful_ribbits.client.renderer.RibbitChestRenderer::new);

        // MenuScreens.register is public on Forge 26.2; registries are already filled by now.
        MenuScreens.register(UsefulRibbitsModMenus.RIBBIT_CHEST_GUI.get(), RibbitChestGUIScreen::new);
        MenuScreens.register(UsefulRibbitsModMenus.RIBBIT_BED_GUI.get(), RibbitBedGUIScreen::new);
    }
}
