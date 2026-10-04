package me.rogue_one.useful_ribbits.fabric.client;

import me.rogue_one.useful_ribbits.client.gui.RibbitBedGUIScreen;
import me.rogue_one.useful_ribbits.client.gui.RibbitChestGUIScreen;
import me.rogue_one.useful_ribbits.client.renderer.ChefRibbitRenderer;
import me.rogue_one.useful_ribbits.client.renderer.FarmerRibbitRenderer;
import me.rogue_one.useful_ribbits.client.renderer.MinerRibbitRenderer;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class UsefulRibbitsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // GeoEntityRenderer resolves geo/animation/texture from assets/useful_ribbits/geckolib.
        EntityRendererRegistry.register(UsefulRibbitsModEntities.CHEF_RIBBIT_SUP.get(), ChefRibbitRenderer::new);
        EntityRendererRegistry.register(UsefulRibbitsModEntities.MINER_RIBBIT_SUP.get(), MinerRibbitRenderer::new);
        EntityRendererRegistry.register(UsefulRibbitsModEntities.FARMER_RIBBIT_SUP.get(), FarmerRibbitRenderer::new);

        MenuScreens.register(UsefulRibbitsModMenus.RIBBIT_CHEST_GUI.get(), RibbitChestGUIScreen::new);
        MenuScreens.register(UsefulRibbitsModMenus.RIBBIT_BED_GUI.get(), RibbitBedGUIScreen::new);

        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlockEntities.RIBBIT_CHEST_SUP.get(), me.rogue_one.useful_ribbits.client.renderer.RibbitChestRenderer::new);
    }
}
