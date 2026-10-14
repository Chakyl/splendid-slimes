package io.github.chakyl.splendidslimes.entity.slimeffects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class SlimeEffect {
    private SlimeEffectType effectType;
    private List<String> commands;
    private List<MobEffectInstance> emitEffects;
    private ParticleOptions effectParticle;

    public SlimeEffect(SlimeEffectType effectType, List<String> commands, List<MobEffectInstance> emitEffects, ParticleOptions effectParticle) {
        this.effectType = effectType;
        this.commands = commands;
        this.emitEffects = emitEffects;
        this.effectParticle = effectParticle;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public SlimeEffectType getEffectType() {
        return effectType;
    }

    public void setEffectType(SlimeEffectType effectType) {
        this.effectType = effectType;
    }

    public List<MobEffectInstance> getEmitEffects() {
        return emitEffects;
    }

    public void setEmitEffects(List<MobEffectInstance> emitEffects) {
        this.emitEffects = emitEffects;
    }

    public ParticleOptions getEffectParticle() {
        return effectParticle;
    }

    public void setEffectParticle(ParticleOptions effectParticle) {
        this.effectParticle = effectParticle;
    }


    public static final Codec<SlimeEffect> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.optionalFieldOf("effect_type", "positive").xmap(
                    s -> switch (s.toLowerCase()) {
                        case "negative" -> SlimeEffectType.NEGATIVE;
                        case "attack" -> SlimeEffectType.ATTACK;
                        default -> SlimeEffectType.POSITIVE;
                    },
                    et -> switch (et) {
                        case NEGATIVE -> "negative";
                        case ATTACK -> "attack";
                        default -> "positive";
                    }
            ).forGetter(SlimeEffect::getEffectType),
            Codec.STRING.listOf().optionalFieldOf("commands", List.of()).forGetter(SlimeEffect::getCommands),
            MobEffectInstance.CODEC.listOf().optionalFieldOf("emit_effects", List.of()).forGetter(SlimeEffect::getEmitEffects),
            ParticleTypes.CODEC.optionalFieldOf("effect_particle",  ParticleTypes.EFFECT).forGetter(SlimeEffect::getEffectParticle)
    ).apply(inst, SlimeEffect::new));
}
