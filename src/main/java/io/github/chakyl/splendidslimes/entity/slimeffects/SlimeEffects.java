package io.github.chakyl.splendidslimes.entity.slimeffects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.chakyl.splendidslimes.entity.slimethetics.SlimeDecorator;
import io.github.chakyl.splendidslimes.entity.slimethetics.SlimeDecorators;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.ArrayList;
import java.util.List;

public class SlimeEffects extends ArrayList<SlimeEffect> {
        public static final Codec<SlimeEffects> CODEC = SlimeEffect.CODEC.listOf().xmap(
                SlimeEffects::new,
                list -> list
        );

        public SlimeEffects() {
        }

        public SlimeEffects(List<SlimeEffect> list) {
            super(list);
        }
    }