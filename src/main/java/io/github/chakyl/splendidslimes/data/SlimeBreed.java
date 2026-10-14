package io.github.chakyl.splendidslimes.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.chakyl.splendidslimes.entity.slimeffects.SlimeEffects;
import io.github.chakyl.splendidslimes.entity.slimethetics.SlimeAppearance;
import io.github.chakyl.splendidslimes.entity.slimetraits.SlimeTraits;
import io.github.chakyl.splendidslimes.item.PlortItem;
import io.github.chakyl.splendidslimes.item.SlimeHeartItem;
import io.github.chakyl.splendidslimes.item.SlimeInventoryItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public record SlimeBreed(String breed, MutableComponent name, TextColor color, SlimeAppearance slimeAppearance,
                         MutableComponent diet, List<Ingredient> foods,
                         ItemStack favoriteFood, List<EntityType<? extends LivingEntity>> entities,
                         Optional<EntityType<? extends LivingEntity>> favoriteEntity,
                         List<EntityType<? extends LivingEntity>> hostileToEntities,
                         List<MobEffectInstance> innateEffects, SlimeEffects slimeEffects,
                         SlimeTraits traits) implements AbstractSlimeBreed {

    public static final Codec<EntityType<? extends LivingEntity>> LIVING_ENTITY_CODEC = (Codec) BuiltInRegistries.ENTITY_TYPE.byNameCodec();

    public static final Codec<SlimeBreed> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("breed").forGetter(SlimeBreed::breed),
            ComponentSerialization.CODEC.xmap(Component::copy, type -> type).fieldOf("name").forGetter(SlimeBreed::name),
            TextColor.CODEC.fieldOf("color").forGetter(SlimeBreed::color),
            SlimeAppearance.CODEC.optionalFieldOf("appearance", new SlimeAppearance()).forGetter(SlimeBreed::slimeAppearance),
            ComponentSerialization.CODEC.xmap(Component::copy, type -> type).fieldOf("diet").forGetter(SlimeBreed::diet),
            Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("foods", List.of()).forGetter(SlimeBreed::foods),
            ItemStack.CODEC.optionalFieldOf("favorite_food", ItemStack.EMPTY).forGetter(SlimeBreed::favoriteFood),
            LIVING_ENTITY_CODEC.listOf().optionalFieldOf("entities", List.of()).forGetter(SlimeBreed::entities),
            LIVING_ENTITY_CODEC.optionalFieldOf("favorite_entity").forGetter(SlimeBreed::favoriteEntity),
            LIVING_ENTITY_CODEC.listOf().optionalFieldOf("hostile_to_entities", List.of()).forGetter(SlimeBreed::hostileToEntities),
            MobEffectInstance.CODEC.listOf().optionalFieldOf("innate_effects", List.of()).forGetter(SlimeBreed::innateEffects),
            SlimeEffects.CODEC.optionalFieldOf("slime_effects", new SlimeEffects()).forGetter(SlimeBreed::slimeEffects),
            SlimeTraits.CODEC.optionalFieldOf("traits", new SlimeTraits()).forGetter(SlimeBreed::traits)
    ).apply(inst, SlimeBreed::new));

    public SlimeBreed(SlimeBreed other) {
        this(other.breed, other.name, other.color, other.slimeAppearance, other.diet, other.foods, other.favoriteFood, other.entities, other.favoriteEntity, other.hostileToEntities, other.innateEffects, other.slimeEffects, other.traits);
    }

    public SlimeBreed validate(ResourceLocation key) {
        Preconditions.checkNotNull(this.breed, "Invalid slime breed id!");
        Preconditions.checkNotNull(this.name, "Invalid slime name!");
        Preconditions.checkNotNull(this.diet, "Invalid slime diet!");
        return this;
    }

    public ItemStack getSlimeItem() {
        ItemStack stack = new ItemStack(ModElements.Items.SLIME_ITEM.value());
        SlimeInventoryItem.setStoredSlime(stack, this);
        return stack;
    }

    @Override
    public Codec<? extends SlimeBreed> getCodec() {
        return CODEC;
    }

    public ItemStack getHeart() {
        ItemStack stack = new ItemStack(ModElements.Items.SLIME_HEART.value());
        SlimeHeartItem.setStoredPlort(stack, this);
        return stack;
    }

    public ItemStack getPlort() {
        ItemStack stack = new ItemStack(ModElements.Items.PLORT.value());
        PlortItem.setStoredPlort(stack, this);
        return stack;
    }

}