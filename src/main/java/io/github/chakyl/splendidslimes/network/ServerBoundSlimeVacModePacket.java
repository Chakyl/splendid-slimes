package io.github.chakyl.splendidslimes.network;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.item.SlimeVac;
import io.github.chakyl.splendidslimes.item.VacMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public record ServerBoundSlimeVacModePacket() implements CustomPacketPayload {
    public static final Type<ServerBoundSlimeVacModePacket> TYPE = new Type<>(loc("slime_vac_mode"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundSlimeVacModePacket> STREAM_CODEC = StreamCodec.unit(new ServerBoundSlimeVacModePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ServerBoundSlimeVacModePacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack vacItem = player.getMainHandItem();
                if (!(vacItem.getItem() instanceof SlimeVac)) {
                    vacItem = player.getOffhandItem();
                }

                if (!(vacItem.getItem() instanceof SlimeVac)) return;

                VacMode mode = SlimeVac.getMode(vacItem);
                switch (mode) {
                    case BOTH -> SlimeVac.setMode(vacItem, VacMode.SLIME);
                    case SLIME -> SlimeVac.setMode(vacItem, VacMode.ITEM);
                    case ITEM -> SlimeVac.setMode(vacItem, VacMode.BOTH);
                }

                Float pitch = 0F;
                switch (mode) {
                    case BOTH -> pitch = 0.7F;
                    case SLIME -> pitch = 0.8F;
                    case ITEM -> pitch = 0.9F;
                }
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),SoundEvents.NOTE_BLOCK_XYLOPHONE.value(), SoundSource.PLAYERS,1.0F, pitch);
                player.displayClientMessage(Component.translatable("info.splendid_slimes.slime_vac.mode_set", SlimeVac.getMode(vacItem).toString()),true);
            }
        });
    }
}