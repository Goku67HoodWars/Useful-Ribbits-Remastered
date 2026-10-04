package me.rogue_one.useful_ribbits.init;

import java.util.function.Function;
import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.block.RibbitBedBlock;
import me.rogue_one.useful_ribbits.block.RibbitChestBlock;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import me.rogue_one.useful_ribbits.registry.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class UsefulRibbitsModBlocks {
    public static final RegistrySupplier<Block> RIBBIT_CHEST_SUP = register("ribbit_chest", RibbitChestBlock::new);
    public static final RegistrySupplier<Block> RIBBIT_BED_SUP = register("ribbit_bed", RibbitBedBlock::new);

    // Raw fields for downstream runtime code (FarmerRibbitGoal / RibbitTransferMeatGoal state.is(...)). Bound after drain.
    public static Block RIBBIT_CHEST;
    public static Block RIBBIT_BED;

    private UsefulRibbitsModBlocks() {}

    public static void load() {}

    public static void bind() {
        RIBBIT_CHEST = RIBBIT_CHEST_SUP.get();
        RIBBIT_BED = RIBBIT_BED_SUP.get();
    }

    private static RegistrySupplier<Block> register(String name, Function<BlockBehaviour.Properties, Block> factory) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, UsefulRibbits.id(name));
        return ModRegistries.BLOCKS.add(name, () -> factory.apply(BlockBehaviour.Properties.of()
                .setId(key)
                .sound(SoundType.WOOD)
                .strength(1.0F, 10.0F)
                .noOcclusion()
                .isRedstoneConductor((state, level, pos) -> false)));
    }
}
