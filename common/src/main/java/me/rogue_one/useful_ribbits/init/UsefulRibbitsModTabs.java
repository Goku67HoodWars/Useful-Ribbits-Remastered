package me.rogue_one.useful_ribbits.init;

import me.rogue_one.useful_ribbits.registry.ModRegistries;
import me.rogue_one.useful_ribbits.registry.RegistrySupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class UsefulRibbitsModTabs {
    public static final RegistrySupplier<CreativeModeTab> USEFUL_RIBBITS_SUP =
            ModRegistries.CREATIVE_TABS.add("useful_ribbits", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("item_group.useful_ribbits.useful_ribbits"))
                    .icon(() -> new ItemStack(UsefulRibbitsModBlocks.RIBBIT_CHEST_SUP.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(UsefulRibbitsModBlocks.RIBBIT_CHEST_SUP.get().asItem());
                        output.accept(UsefulRibbitsModBlocks.RIBBIT_BED_SUP.get().asItem());
                        output.accept(UsefulRibbitsModItems.CHEF_RIBBIT_SPAWN_EGG_SUP.get());
                        output.accept(UsefulRibbitsModItems.MINER_RIBBIT_SPAWN_EGG_SUP.get());
                        output.accept(UsefulRibbitsModItems.FARMER_RIBBIT_SPAWN_EGG_SUP.get());
                    })
                    .build());

    // Raw field for parity/extension. Bound after drain.
    public static CreativeModeTab USEFUL_RIBBITS;

    private UsefulRibbitsModTabs() {}

    public static void load() {}

    public static void bind() {
        USEFUL_RIBBITS = USEFUL_RIBBITS_SUP.get();
    }
}
