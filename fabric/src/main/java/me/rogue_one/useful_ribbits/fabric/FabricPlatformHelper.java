package me.rogue_one.useful_ribbits.fabric;

import me.rogue_one.useful_ribbits.platform.IPlatformHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public <T extends CustomPacketPayload> void sendToServer(T payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> menuType(MenuType.MenuSupplier<T> factory) {
        return new MenuType<>(factory, FeatureFlags.VANILLA_SET);
    }

    @Override
    public java.nio.file.Path getConfigFolder() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir();
    }
}
