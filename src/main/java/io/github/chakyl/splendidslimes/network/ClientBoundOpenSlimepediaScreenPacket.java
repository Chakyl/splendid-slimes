package io.github.chakyl.splendidslimes.network;

import io.github.chakyl.splendidslimes.screen.SlimepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;


public record ClientBoundOpenSlimepediaScreenPacket(List<String> unlockedSlimeBreeds) implements CustomPacketPayload {
    public static final Type<ClientBoundOpenSlimepediaScreenPacket> TYPE = new Type<>(loc("open_slimepedia"));

    @Override
    public Type<ClientBoundOpenSlimepediaScreenPacket> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundOpenSlimepediaScreenPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8),
            ClientBoundOpenSlimepediaScreenPacket::unlockedSlimeBreeds,
            ClientBoundOpenSlimepediaScreenPacket::new
    );

    public static void handle(ClientBoundOpenSlimepediaScreenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new SlimepediaScreen(packet.unlockedSlimeBreeds()));
        });
    }
}