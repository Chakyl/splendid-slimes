package io.github.chakyl.splendidslimes.network;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class PacketHandler {
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            loc("main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> String.valueOf(1))
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(SlimeVacModePacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SlimeVacModePacket::encode)
                .decoder(SlimeVacModePacket::new)
                .consumerMainThread(SlimeVacModePacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }
}
