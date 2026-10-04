package me.rogue_one.useful_ribbits.neoforge.client;

import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.client.gui.RibbitBedGUIScreen;
import me.rogue_one.useful_ribbits.client.gui.RibbitChestGUIScreen;
import me.rogue_one.useful_ribbits.client.renderer.ChefRibbitRenderer;
import me.rogue_one.useful_ribbits.client.renderer.FarmerRibbitRenderer;
import me.rogue_one.useful_ribbits.client.renderer.MinerRibbitRenderer;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = UsefulRibbits.MODID, value = Dist.CLIENT)
public class UsefulRibbitsNeoForgeClient {

    @SubscribeEvent
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // GeoEntityRenderer needs no layer definitions; GeckoLib loads the model from resources.
        event.registerEntityRenderer(UsefulRibbitsModEntities.CHEF_RIBBIT_SUP.get(), ChefRibbitRenderer::new);
        event.registerEntityRenderer(UsefulRibbitsModEntities.MINER_RIBBIT_SUP.get(), MinerRibbitRenderer::new);
        event.registerEntityRenderer(UsefulRibbitsModEntities.FARMER_RIBBIT_SUP.get(), FarmerRibbitRenderer::new);
        event.registerBlockEntityRenderer(me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlockEntities.RIBBIT_CHEST_SUP.get(), me.rogue_one.useful_ribbits.client.renderer.RibbitChestRenderer::new);
    }

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(UsefulRibbitsModMenus.RIBBIT_CHEST_GUI.get(), RibbitChestGUIScreen::new);
        event.register(UsefulRibbitsModMenus.RIBBIT_BED_GUI.get(), RibbitBedGUIScreen::new);
    }
}
