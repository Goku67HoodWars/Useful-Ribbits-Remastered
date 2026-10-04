package me.rogue_one.useful_ribbits.forge;

import me.rogue_one.useful_ribbits.forge.module.NetworkModuleForge;
import me.rogue_one.useful_ribbits.platform.IPlatformHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public <T extends CustomPacketPayload> void sendToServer(T payload) {
        NetworkModuleForge.sendToServer(payload);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> menuType(MenuType.MenuSupplier<T> factory) {
        return IForgeMenuType.create((windowId, inv, buf) -> factory.create(windowId, inv));
    }

    @Override
    public java.nio.file.Path getConfigFolder() {
        return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean useCustomEntityPreview() {
        return true;
    }
}
