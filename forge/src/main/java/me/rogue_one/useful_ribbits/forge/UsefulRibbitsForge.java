package me.rogue_one.useful_ribbits.forge;

import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.forge.client.UsefulRibbitsForgeClient;
import me.rogue_one.useful_ribbits.forge.module.NetworkModuleForge;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import me.rogue_one.useful_ribbits.registry.DeferredRegistry;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

@Mod(UsefulRibbits.MODID)
public class UsefulRibbitsForge {

    public UsefulRibbitsForge(FMLJavaModLoadingContext context) {
        BusGroup modBus = context.getModBusGroup();

        me.rogue_one.useful_ribbits.config.UsefulRibbitsConfig.load();
        ModRegistries.bootstrap();

        RegisterEvent.getBus(modBus).addListener(UsefulRibbitsForge::onRegister);
        EntityAttributeCreationEvent.BUS.addListener(UsefulRibbitsForge::onAttributes);
        // Raw fields can only be bound once every RegisterEvent has filled its registry.
        FMLCommonSetupEvent.getBus(modBus).addListener(event -> event.enqueueWork(ModRegistries::bindRawFields));

        NetworkModuleForge.register();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            UsefulRibbitsForgeClient.init();
        }
    }

    private static void onRegister(RegisterEvent event) {
        for (DeferredRegistry<?> registry : ModRegistries.all()) {
            register(event, registry);
        }
    }

    private static <T> void register(RegisterEvent event, DeferredRegistry<T> registry) {
        registry.forEach((id, value) -> event.register(registry.registryKey(), id, value::get));
    }

    private static void onAttributes(EntityAttributeCreationEvent event) {
        UsefulRibbitsModEntities.forEachAttribute((type, builder) -> event.put(type, builder.build()));
    }
}
