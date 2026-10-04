package me.rogue_one.useful_ribbits.init;

import me.rogue_one.useful_ribbits.platform.PlatformHelper;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import me.rogue_one.useful_ribbits.registry.RegistrySupplier;
import me.rogue_one.useful_ribbits.world.inventory.RibbitBedGUIMenu;
import me.rogue_one.useful_ribbits.world.inventory.RibbitChestGUIMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Menu types. The MenuType ctor is private on vanilla, so each type is built through
 * {@link PlatformHelper#menuType} (Fabric: access-widened ctor; Forge: IForgeMenuType.create;
 * NeoForge: IMenuTypeExtension.create). Both menus open via vanilla Player.openMenu(MenuProvider),
 * so the IContainerFactory buffer is ignored.
 */
public final class UsefulRibbitsModMenus {
    public static final RegistrySupplier<MenuType<RibbitChestGUIMenu>> RIBBIT_CHEST_GUI =
            ModRegistries.MENUS.add("ribbit_chest_gui", () -> PlatformHelper.menuType(RibbitChestGUIMenu::new));
    public static final RegistrySupplier<MenuType<RibbitBedGUIMenu>> RIBBIT_BED_GUI =
            ModRegistries.MENUS.add("ribbit_bed_gui", () -> PlatformHelper.menuType(RibbitBedGUIMenu::new));

    private UsefulRibbitsModMenus() {}

    public static void load() {}

    public static void bind() {}
}
