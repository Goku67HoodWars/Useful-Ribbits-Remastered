package me.rogue_one.useful_ribbits.neoforge.module;

import me.rogue_one.useful_ribbits.network.RibbitBedGUIButtonMessage;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = "useful_ribbits")
public class NetworkModuleNeoForge {
   private static final String PROTOCOL_VERSION = "1";

   @SubscribeEvent
   public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
      var registrar = event.registrar(PROTOCOL_VERSION);
      registrar.playToServer(RibbitBedGUIButtonMessage.TYPE, RibbitBedGUIButtonMessage.STREAM_CODEC,
         (payload, context) -> context.enqueueWork(() ->
            RibbitBedGUIButtonMessage.handle((ServerPlayer) context.player(), payload)));
   }
}
