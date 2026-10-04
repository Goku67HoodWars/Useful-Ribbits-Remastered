package me.rogue_one.useful_ribbits.fabric;

import me.rogue_one.useful_ribbits.fabric.module.NetworkModuleFabric;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import me.rogue_one.useful_ribbits.registry.DeferredRegistry;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class UsefulRibbitsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Fabric registries are open during mod init, so the queued entries go straight in.
        me.rogue_one.useful_ribbits.config.UsefulRibbitsConfig.load();
        ModRegistries.bootstrap();
        ModRegistries.all().forEach(DeferredRegistry::registerAll);
        ModRegistries.bindRawFields();
        UsefulRibbitsModEntities.forEachAttribute(FabricDefaultAttributeRegistry::register);
        NetworkModuleFabric.register();
    }
}
