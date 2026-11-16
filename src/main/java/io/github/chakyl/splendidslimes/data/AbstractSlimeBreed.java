package io.github.chakyl.splendidslimes.data;

import dev.shadowsoffire.placebo.codec.CodecProvider;
import io.github.chakyl.splendidslimes.entity.slimeffects.SlimeEffects;
import io.github.chakyl.splendidslimes.entity.slimethetics.SlimeAppearance;
import io.github.chakyl.splendidslimes.entity.slimetraits.SlimeTraits;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public sealed interface AbstractSlimeBreed extends CodecProvider<SlimeBreed> permits SlimeBreed {

    String breed();

    MutableComponent name();

    TextColor color();

    SlimeAppearance slimeAppearance();

    MutableComponent diet();

    List<Ingredient> foods();

    ItemStack favoriteFood();

    List<EntityType<? extends LivingEntity>> entities();

    Optional<EntityType<? extends LivingEntity>> favoriteEntity();

    List<EntityType<? extends LivingEntity>> hostileToEntities();

    List<MobEffectInstance> innateEffects();

    SlimeEffects slimeEffects();

    SlimeTraits traits();

}