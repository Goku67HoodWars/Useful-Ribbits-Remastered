package me.rogue_one.useful_ribbits.registry;

import java.util.List;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlockEntities;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModBlocks;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModEntities;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModItems;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModMenus;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModSounds;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Every registry this mod contributes to, in creation order.
 * <p>
 * Blocks come before block-entities and items (both look a block up as they build); entities come
 * before items (spawn eggs resolve their EntityType as they build); the creative tab comes last (it
 * lists blocks and items). Because {@link RegistrySupplier#get()} builds lazily, this ordering is a
 * belt-and-braces nicety rather than a hard requirement -- it is enough that every entry is queued
 * (via {@link #bootstrap()}) before the first drain.
 */
public final class ModRegistries {
    public static final DeferredRegistry<SoundEvent> SOUND_EVENTS =
            DeferredRegistry.of(BuiltInRegistries.SOUND_EVENT, Registries.SOUND_EVENT);
    public static final DeferredRegistry<Block> BLOCKS =
            DeferredRegistry.of(BuiltInRegistries.BLOCK, Registries.BLOCK);
    public static final DeferredRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegistry.of(BuiltInRegistries.BLOCK_ENTITY_TYPE, Registries.BLOCK_ENTITY_TYPE);
    public static final DeferredRegistry<EntityType<?>> ENTITY_TYPES =
            DeferredRegistry.of(BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE);
    public static final DeferredRegistry<Item> ITEMS =
            DeferredRegistry.of(BuiltInRegistries.ITEM, Registries.ITEM);
    public static final DeferredRegistry<MenuType<?>> MENUS =
            DeferredRegistry.of(BuiltInRegistries.MENU, Registries.MENU);
    public static final DeferredRegistry<CreativeModeTab> CREATIVE_TABS =
            DeferredRegistry.of(BuiltInRegistries.CREATIVE_MODE_TAB, Registries.CREATIVE_MODE_TAB);

    private static final List<DeferredRegistry<?>> ALL = List.of(
            SOUND_EVENTS, BLOCKS, BLOCK_ENTITY_TYPES, ENTITY_TYPES, ITEMS, MENUS, CREATIVE_TABS);

    private ModRegistries() {}

    /** All registries, in creation order. */
    public static List<DeferredRegistry<?>> all() {
        return ALL;
    }

    /**
     * Forces each init module to class-load so its static entries queue into the deferred
     * registries. Call before any drain (Fabric onInitialize; Forge/NeoForge constructor).
     */
    public static void bootstrap() {
        UsefulRibbitsModSounds.load();
        UsefulRibbitsModBlocks.load();
        UsefulRibbitsModBlockEntities.load();
        UsefulRibbitsModEntities.load();
        UsefulRibbitsModItems.load();
        UsefulRibbitsModMenus.load();
        UsefulRibbitsModTabs.load();
    }

    /**
     * Copies each built value into the init modules' raw static fields, so the rest of the mod can
     * keep reading e.g. {@code UsefulRibbitsModSounds.RIBBIT_AMBIANT} unchanged. Call AFTER every
     * registry has been drained -- Fabric: immediately after registerAll; Forge/NeoForge: inside
     * FMLCommonSetupEvent#enqueueWork (all RegisterEvents have fired by then).
     */
    public static void bindRawFields() {
        UsefulRibbitsModSounds.bind();
        UsefulRibbitsModBlocks.bind();
        UsefulRibbitsModBlockEntities.bind();
        UsefulRibbitsModEntities.bind();
        UsefulRibbitsModItems.bind();
        UsefulRibbitsModMenus.bind();
        UsefulRibbitsModTabs.bind();
    }
}
