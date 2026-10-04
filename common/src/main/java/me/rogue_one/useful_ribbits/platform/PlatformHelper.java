package me.rogue_one.useful_ribbits.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.ServiceLoader;

public final class PlatformHelper {
    private static final IPlatformHelper SERVICE = ServiceLoader.load(IPlatformHelper.class).findFirst().orElseThrow();

    private PlatformHelper() {}

    public static IPlatformHelper getPlatformService() {
        return SERVICE;
    }

    public static <T extends CustomPacketPayload> void sendToServer(T payload) {
        SERVICE.sendToServer(payload);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> menuType(MenuType.MenuSupplier<T> factory) {
        return SERVICE.menuType(factory);
    }

    public static java.nio.file.Path getConfigFolder() {
        return SERVICE.getConfigFolder();
    }
}
