package io.github.chakyl.splendidslimes.network;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SplendidSlimes.MODID)
public class PacketHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                ClientBoundOpenSlimepediaScreenPacket.TYPE,
                ClientBoundOpenSlimepediaScreenPacket.STREAM_CODEC,
                ClientBoundOpenSlimepediaScreenPacket::handle
        );
    }

    public static <MSG extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
}