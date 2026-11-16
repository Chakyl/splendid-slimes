package io.github.chakyl.splendidslimes.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


public record SlimeDataComponent(CompoundTag entity) {
    public static final Codec<SlimeDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompoundTag.CODEC.fieldOf("entity").forGetter(SlimeDataComponent::entity)
            ).apply(instance, SlimeDataComponent::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SlimeDataComponent::entity,
            SlimeDataComponent::new
    );
}