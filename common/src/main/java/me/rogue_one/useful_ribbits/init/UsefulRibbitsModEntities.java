package me.rogue_one.useful_ribbits.init;

import java.util.function.BiConsumer;
import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.entity.ChefRibbitEntity;
import me.rogue_one.useful_ribbits.entity.FarmerRibbitEntity;
import me.rogue_one.useful_ribbits.entity.MinerRibbitEntity;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import me.rogue_one.useful_ribbits.registry.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public final class UsefulRibbitsModEntities {
    public static final RegistrySupplier<EntityType<ChefRibbitEntity>> CHEF_RIBBIT_SUP = register(
            "chef_ribbit", EntityType.Builder.of(ChefRibbitEntity::new, MobCategory.CREATURE).sized(0.5F, 0.8F).clientTrackingRange(64).updateInterval(3));
    public static final RegistrySupplier<EntityType<MinerRibbitEntity>> MINER_RIBBIT_SUP = register(
            "miner_ribbit", EntityType.Builder.of(MinerRibbitEntity::new, MobCategory.CREATURE).sized(0.5F, 0.7F).clientTrackingRange(64).updateInterval(3));
    public static final RegistrySupplier<EntityType<FarmerRibbitEntity>> FARMER_RIBBIT_SUP = register(
            "farmer_ribbit", EntityType.Builder.of(FarmerRibbitEntity::new, MobCategory.CREATURE).sized(0.5F, 0.7F).clientTrackingRange(64).updateInterval(3));

    // Raw fields for downstream runtime code (spawn-egg items via suppliers use these indirectly;
    // procedures/renderers read these). Bound after drain.
    public static EntityType<ChefRibbitEntity> CHEF_RIBBIT;
    public static EntityType<MinerRibbitEntity> MINER_RIBBIT;
    public static EntityType<FarmerRibbitEntity> FARMER_RIBBIT;

    private UsefulRibbitsModEntities() {}

    public static void load() {}

    public static void bind() {
        CHEF_RIBBIT = CHEF_RIBBIT_SUP.get();
        MINER_RIBBIT = MINER_RIBBIT_SUP.get();
        FARMER_RIBBIT = FARMER_RIBBIT_SUP.get();
    }

    /**
     * Loader-agnostic default-attribute wiring. Each loader passes its own sink:
     *   Fabric  -> FabricDefaultAttributeRegistry::register  (takes the Builder directly)
     *   Forge/NeoForge -> (type, builder) -> event.put(type, builder.build())
     */
    public static void forEachAttribute(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> sink) {
        sink.accept(CHEF_RIBBIT_SUP.get(), ChefRibbitEntity.createAttributes());
        sink.accept(MINER_RIBBIT_SUP.get(), MinerRibbitEntity.createAttributes());
        sink.accept(FARMER_RIBBIT_SUP.get(), FarmerRibbitEntity.createAttributes());
    }

    private static <T extends Entity> RegistrySupplier<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, UsefulRibbits.id(name));
        return ModRegistries.ENTITY_TYPES.add(name, () -> builder.build(key));
    }
}
