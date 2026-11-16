package io.github.chakyl.splendidslimes.entity.slimethetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class SlimeAppearance {
    private SlimeDecorators slimeDecorator;
    private ParticleOptions walkParticle;
    private ItemStack itemWalkParticle;
    private Holder<SoundEvent> walkSound;

    public SlimeAppearance(SlimeDecorators slimeDecorator, ParticleOptions walkParticle, ItemStack itemWalkParticle, Holder<SoundEvent> walkSound) {
        this.slimeDecorator = slimeDecorator;
        this.walkParticle = walkParticle;
        this.itemWalkParticle = itemWalkParticle;
        this.walkSound = walkSound;
    }

    public SlimeAppearance() {
    }

    public ParticleOptions getWalkParticle() {
        return walkParticle;
    }

    public void setWalkParticle(SimpleParticleType walkParticle) {
        this.walkParticle = walkParticle;
    }

    public SlimeDecorators getSlimeDecorators() {
        return slimeDecorator;
    }

    public void setSlimeDecorator(SlimeDecorators slimeDecorator) {
        this.slimeDecorator = slimeDecorator;
    }

    public ItemStack getItemWalkParticle() {
        return itemWalkParticle;
    }

    public void setItemWalkParticle(ItemStack itemWalkParticle) {
        this.itemWalkParticle = itemWalkParticle;
    }

    public Holder<SoundEvent> getWalkSound() {
        return walkSound;
    }

    public void setWalkSound(Holder<SoundEvent> walkSound) {
        this.walkSound = walkSound;
    }

    public static final Codec<SlimeAppearance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SlimeDecorators.CODEC.optionalFieldOf("decorators", new SlimeDecorators()).forGetter(SlimeAppearance::getSlimeDecorators),
            ParticleTypes.CODEC.optionalFieldOf("walk_particle", ParticleTypes.EFFECT).forGetter(SlimeAppearance::getWalkParticle),
            ItemStack.CODEC.optionalFieldOf("item_walk_particle", ItemStack.EMPTY).forGetter(SlimeAppearance::getItemWalkParticle),
            BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("walk_sound", BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SLIME_SQUISH_SMALL)).forGetter(SlimeAppearance::getWalkSound)
    ).apply(inst, SlimeAppearance::new));
}
