package me.rogue_one.useful_ribbits.fabric.module;

import me.rogue_one.useful_ribbits.network.RibbitBedGUIButtonMessage;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class NetworkModuleFabric {
   public static void register() {
      PayloadTypeRegistry.serverboundPlay().register(RibbitBedGUIButtonMessage.TYPE, RibbitBedGUIButtonMessage.STREAM_CODEC);
      ServerPlayNetworking.registerGlobalReceiver(RibbitBedGUIButtonMessage.TYPE, (payload, context) -> {
         ServerPlayer player = context.player();
         context.server().execute(() -> RibbitBedGUIButtonMessage.handle(player, payload));
      });
   }
}
