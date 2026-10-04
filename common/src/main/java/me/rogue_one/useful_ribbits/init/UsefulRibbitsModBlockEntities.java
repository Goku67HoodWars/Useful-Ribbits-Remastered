package me.rogue_one.useful_ribbits.init;

import java.util.Set;
import me.rogue_one.useful_ribbits.block.entity.RibbitChestBlockEntity;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import me.rogue_one.useful_ribbits.registry.RegistrySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class UsefulRibbitsModBlockEntities {
    public static final RegistrySupplier<BlockEntityType<RibbitChestBlockEntity>> RIBBIT_CHEST_SUP =
            ModRegistries.BLOCK_ENTITY_TYPES.add("ribbit_chest",
                    () -> new BlockEntityType<>(RibbitChestBlockEntity::new, Set.of(UsefulRibbitsModBlocks.RIBBIT_CHEST_SUP.get())));

    // Raw field for downstream runtime code (RibbitChestBlockEntity ctor super(...)). Bound after drain.
    public static BlockEntityType<RibbitChestBlockEntity> RIBBIT_CHEST;

    private UsefulRibbitsModBlockEntities() {}

    public static void load() {}

    public static void bind() {
        RIBBIT_CHEST = RIBBIT_CHEST_SUP.get();
    }
}
