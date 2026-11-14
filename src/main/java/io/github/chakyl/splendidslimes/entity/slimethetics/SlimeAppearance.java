package io.github.chakyl.splendidslimes.entity.slimethetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SlimeAppearance {
    private SlimeDecorators slimeDecorator;
    private ParticleOptions walkParticle;
    private ItemStack itemWalkParticle;

    public SlimeAppearance(SlimeDecorators slimeDecorator, ParticleOptions walkParticle, ItemStack itemWalkParticle) {
        this.slimeDecorator = slimeDecorator;
        this.walkParticle = walkParticle;
        this.itemWalkParticle = itemWalkParticle;
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


    public static final Codec<SlimeAppearance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SlimeDecorators.CODEC.optionalFieldOf("decorators", new SlimeDecorators()).forGetter(SlimeAppearance::getSlimeDecorators),
            ParticleTypes.CODEC.optionalFieldOf("walk_particle", ParticleTypes.EFFECT).forGetter(SlimeAppearance::getWalkParticle),
            ItemStack.CODEC.optionalFieldOf("item_walk_particle", ItemStack.EMPTY).forGetter(SlimeAppearance::getItemWalkParticle)
    ).apply(inst, SlimeAppearance::new));
}
