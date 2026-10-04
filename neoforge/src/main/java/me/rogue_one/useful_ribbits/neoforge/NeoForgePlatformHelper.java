package me.rogue_one.useful_ribbits.neoforge;

import me.rogue_one.useful_ribbits.platform.IPlatformHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public <T extends CustomPacketPayload> void sendToServer(T payload) {
        // Client-only class; resolved lazily, invoked only client-side (never on a dedicated server).
        ClientPacketDistributor.sendToServer(payload);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> menuType(MenuType.MenuSupplier<T> factory) {
        return IMenuTypeExtension.create((windowId, inv, buf) -> factory.create(windowId, inv));
    }

    @Override
    public java.nio.file.Path getConfigFolder() {
        return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
    }
}
