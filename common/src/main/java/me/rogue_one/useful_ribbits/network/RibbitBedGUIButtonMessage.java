package me.rogue_one.useful_ribbits.network;

import me.rogue_one.useful_ribbits.procedures.RBGUIChefBtnProcedure;
import me.rogue_one.useful_ribbits.procedures.RBGUIFarmerBtnProcedure;
import me.rogue_one.useful_ribbits.procedures.RBGUIMinerBtnProcedure;
import me.rogue_one.useful_ribbits.world.inventory.RibbitBedGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * C2S packet fired when a job button in the Ribbit Bed GUI is pressed.
 *
 * <p>Only the button id travels over the wire. The client menu never learns the bed's position;
 * the server reads it from its own {@link RibbitBedGUIMenu} instance (set by the block when it
 * opened the menu). Loader-agnostic: each loader's NetworkModule registers {@link #TYPE}/
 * {@link #STREAM_CODEC} and routes its receiver to {@link #handle(ServerPlayer, RibbitBedGUIButtonMessage)}.
 */
public record RibbitBedGUIButtonMessage(int buttonID) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<RibbitBedGUIButtonMessage> TYPE = new CustomPacketPayload.Type<>(
      Identifier.fromNamespaceAndPath("useful_ribbits", "ribbit_bed_gui_button")
   );

   public static final StreamCodec<RegistryFriendlyByteBuf, RibbitBedGUIButtonMessage> STREAM_CODEC = CustomPacketPayload.codec(
      (message, buffer) -> buffer.writeInt(message.buttonID),
      buffer -> new RibbitBedGUIButtonMessage(buffer.readInt())
   );

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   /** Server-side handling shared by all loaders. MUST be invoked on the server thread. */
   public static void handle(ServerPlayer player, RibbitBedGUIButtonMessage message) {
      if (player != null && player.containerMenu instanceof RibbitBedGUIMenu menu) {
         BlockPos pos = new BlockPos(menu.x, menu.y, menu.z);
         // The client menu's SimpleContainer stillValid() is always true, so the bed GUI never
         // auto-closes; guard server-side that the bed still exists and the player is near it.
         if (player.level().getBlockState(pos).getBlock() instanceof me.rogue_one.useful_ribbits.block.RibbitBedBlock
               && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0) {
            handleButtonAction(player, message.buttonID(), menu.x, menu.y, menu.z);
         }
      }
   }

   public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
      Level world = entity.level();
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            RBGUIMinerBtnProcedure.execute(world, x, y, z, entity);
         }

         if (buttonID == 1) {
            RBGUIChefBtnProcedure.execute(world, x, y, z, entity);
         }

         if (buttonID == 2) {
            RBGUIFarmerBtnProcedure.execute(world, x, y, z, entity);
         }
      }
   }
}
