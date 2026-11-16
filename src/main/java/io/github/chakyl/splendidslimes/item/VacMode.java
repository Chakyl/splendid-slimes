package io.github.chakyl.splendidslimes.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum VacMode implements StringRepresentable {
    ITEM,
    SLIME,
    BOTH;

    public static final Codec<VacMode> CODEC = StringRepresentable.fromEnum(VacMode::values);
    public static final StreamCodec<ByteBuf, VacMode> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}