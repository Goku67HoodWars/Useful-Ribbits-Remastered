package me.rogue_one.useful_ribbits.forge.module;

import me.rogue_one.useful_ribbits.network.RibbitBedGUIButtonMessage;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;

/**
 * One optional channel carrying the single C2S button packet. The payload codec is written against
 * RegistryFriendlyByteBuf, so it is {@code .cast()} to the play protocol's buffer, exactly as the
 * Ribbits template does. The handler hops to the server thread before touching the player's menu.
 */
public class NetworkModuleForge {
   private static final int PROTOCOL_VERSION = 1;

   private static Channel<CustomPacketPayload> channel;

   public static void register() {
      channel = ChannelBuilder.named(Identifier.fromNamespaceAndPath("useful_ribbits", "main"))
         .networkProtocolVersion(PROTOCOL_VERSION)
         .optional()
         .payloadChannel()
         .play()
         .serverbound()
         .addMain(RibbitBedGUIButtonMessage.TYPE, RibbitBedGUIButtonMessage.STREAM_CODEC.cast(),
            (payload, context) -> context.enqueueWork(() ->
               RibbitBedGUIButtonMessage.handle(context.getSender(), payload)))
         .build();
   }

   public static Channel<CustomPacketPayload> channel() {
      if (channel == null) {
         throw new IllegalStateException("useful_ribbits network channel was used before it was registered");
      }
      return channel;
   }

   public static <T extends CustomPacketPayload> void sendToServer(T payload) {
      channel().send(payload, PacketDistributor.SERVER.noArg());
   }
}
