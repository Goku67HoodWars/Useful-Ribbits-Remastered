package me.rogue_one.useful_ribbits.init;

import java.util.function.Supplier;
import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import me.rogue_one.useful_ribbits.registry.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

public final class UsefulRibbitsModItems {
    public static final RegistrySupplier<Item> RIBBIT_CHEST_SUP = blockItem("ribbit_chest", UsefulRibbitsModBlocks.RIBBIT_CHEST_SUP);
    public static final RegistrySupplier<Item> RIBBIT_BED_SUP = blockItem("ribbit_bed", UsefulRibbitsModBlocks.RIBBIT_BED_SUP);
    public static final RegistrySupplier<Item> CHEF_RIBBIT_SPAWN_EGG_SUP = spawnEgg("chef_ribbit_spawn_egg", UsefulRibbitsModEntities.CHEF_RIBBIT_SUP);
    public static final RegistrySupplier<Item> MINER_RIBBIT_SPAWN_EGG_SUP = spawnEgg("miner_ribbit_spawn_egg", UsefulRibbitsModEntities.MINER_RIBBIT_SUP);
    public static final RegistrySupplier<Item> FARMER_RIBBIT_SPAWN_EGG_SUP = spawnEgg("farmer_ribbit_spawn_egg", UsefulRibbitsModEntities.FARMER_RIBBIT_SUP);

    // Raw fields for downstream runtime code (tab display uses suppliers; these are for parity/extension). Bound after drain.
    public static Item RIBBIT_CHEST;
    public static Item RIBBIT_BED;
    public static Item CHEF_RIBBIT_SPAWN_EGG;
    public static Item MINER_RIBBIT_SPAWN_EGG;
    public static Item FARMER_RIBBIT_SPAWN_EGG;

    private UsefulRibbitsModItems() {}

    public static void load() {}

    public static void bind() {
        RIBBIT_CHEST = RIBBIT_CHEST_SUP.get();
        RIBBIT_BED = RIBBIT_BED_SUP.get();
        CHEF_RIBBIT_SPAWN_EGG = CHEF_RIBBIT_SPAWN_EGG_SUP.get();
        MINER_RIBBIT_SPAWN_EGG = MINER_RIBBIT_SPAWN_EGG_SUP.get();
        FARMER_RIBBIT_SPAWN_EGG = FARMER_RIBBIT_SPAWN_EGG_SUP.get();
    }

    private static RegistrySupplier<Item> blockItem(String name, Supplier<? extends Block> block) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, UsefulRibbits.id(name));
        return ModRegistries.ITEMS.add(name, () -> new BlockItem(block.get(), new Item.Properties().setId(key).useBlockDescriptionPrefix()));
    }

    private static RegistrySupplier<Item> spawnEgg(String name, Supplier<? extends EntityType<?>> type) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, UsefulRibbits.id(name));
        return ModRegistries.ITEMS.add(name, () -> new SpawnEggItem(new Item.Properties().setId(key).spawnEgg(type.get())));
    }
}
