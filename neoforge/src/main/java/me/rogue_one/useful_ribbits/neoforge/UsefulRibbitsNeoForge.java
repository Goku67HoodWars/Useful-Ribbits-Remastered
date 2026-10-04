package me.rogue_one.useful_ribbits.neoforge;

import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import me.rogue_one.useful_ribbits.registry.DeferredRegistry;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(UsefulRibbits.MODID)
public class UsefulRibbitsNeoForge {

    public UsefulRibbitsNeoForge(IEventBus eventBus, ModContainer container) {
        me.rogue_one.useful_ribbits.config.UsefulRibbitsConfig.load();
        ModRegistries.bootstrap();

        eventBus.addListener(UsefulRibbitsNeoForge::onRegister);
        eventBus.addListener(UsefulRibbitsNeoForge::onAttributes);
        eventBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(ModRegistries::bindRawFields));
        // NetworkModuleNeoForge and UsefulRibbitsNeoForgeClient self-register via @EventBusSubscriber.
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
