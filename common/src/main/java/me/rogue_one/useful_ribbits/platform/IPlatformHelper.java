package me.rogue_one.useful_ribbits.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/** The two loader-specific operations Useful Ribbits needs: a C2S send and a MenuType factory. */
public interface IPlatformHelper {
    <T extends CustomPacketPayload> void sendToServer(T payload);

    <T extends AbstractContainerMenu> MenuType<T> menuType(MenuType.MenuSupplier<T> factory);

    java.nio.file.Path getConfigFolder();

    /**
     * Whether to render bed ribbit previews through our per-slot picture-in-picture renderers instead of
     * vanilla's shared {@code GuiEntityRenderer}. True only on Forge, whose deferred GUI blit collapses
     * all previews to the last one when one entity pip renderer is reused across a frame.
     */
    default boolean useCustomEntityPreview() {
        return false;
    }
}
