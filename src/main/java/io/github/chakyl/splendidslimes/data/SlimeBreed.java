package io.github.chakyl.splendidslimes.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.json.ItemAdapter;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.item.PlortItem;
import io.github.chakyl.splendidslimes.item.SlimeInventoryItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.util.SlimeData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.chakyl.splendidslimes.util.SlimeBreedRegistration.*;

/**
 * Stores all of the information representing a Slime.
 *
 * @param breed               The breed of the slime
 * @param name                The display name of slime
 * @param particle            Particle used when slime jumps. Defaults to its Plort
 * @param hat                 Item to use as a hat
 * @param hatScale            Scale applied to the hat when it is being rendered.
 * @param hatXOffset          X offset applied to the hat when it is being rendered.
 * @param hatYOffset          Y offset applied to the hat when it is being rendered.
 * @param hatZOffset          Z offset applied to the hat when it is being rendered.
 * @param diet                Diet of the slime, for players. Be as vague or mysterious as needed
 * @param foods               List of items or item tags a Slime will eat
 * @param favoriteFood        The itemstack for a slime's favorite food that doubles plort output
 * @param entities            List of Entities a Slime will attack and eat
 * @param favoriteEntity      The Entity for a slime's favorite food that doubles plort output
 * @param hostileToEntities   List of Entities a Slime will attack and NOT eat
 * @param traits              List of Slime traits. Documentation for each trait can be found on the wiki
 * @param innateEffects       List of effects applied infinitely to the Slime
 * @param emitEffectParticle  Particle type created when a Slime emits positive/negative effects
 * @param positiveEmitEffects List of effects that will be emitted in an AoE around the slime when happy
 * @param negativeEmitEffects List of effects that will be emitted in an AoE around the slime when unhappy
 * @param positiveCommands    List of commands that will be executed as the slime when happy
 * @param negativeCommands    List of commands that will be executed as the slime when unhappy
 * @param attackCommands      List of commands that will be executed periodically when a slime targets a "hostileToEntities" entity
 */
public record SlimeBreed(String breed, MutableComponent name,
                         ItemStack hat, float hatScale, float hatXOffset, float hatYOffset, float hatZOffset,
                         ItemStack particle, MutableComponent diet, List<Object> foods, ItemStack favoriteFood,
                         List<EntityType<? extends LivingEntity>> entities,
                         EntityType<? extends LivingEntity> favoriteEntity,
                         List<EntityType<? extends LivingEntity>> hostileToEntities,
                         List<String> traits,
                         List<MobEffectInstance> innateEffects,
                         SimpleParticleType emitEffectParticle,
                         List<MobEffectInstance> positiveEmitEffects,
                         List<MobEffectInstance> negativeEmitEffects,
                         List<String> positiveCommands,
                         List<String> negativeCommands,
                         List<String> attackCommands) implements CodecProvider<SlimeBreed> {

    public static final Codec<SlimeBreed> CODEC = new SlimeBreedCodec();
    public static final List<String> POSSIBLE_TRAITS = Arrays.asList("aquatic", "defiant", "dominant", "recessive", "explosive", "feral", "flaming", "floating", "foodporting", "friendly", "diverse", "handy", "inverse", "largoless", "moody", "nuclear", "picky", "photosynthesizing", "putrid", "spiky", "weeping");

    public SlimeBreed(SlimeBreed other) {
        this(other.breed, other.name, other.hat, other.hatScale, other.hatXOffset, other.hatYOffset, other.hatZOffset, other.particle, other.diet, other.foods, other.favoriteFood, other.entities, other.favoriteEntity, other.hostileToEntities, other.traits, other.innateEffects, other.emitEffectParticle, other.positiveEmitEffects, other.negativeEmitEffects, other.positiveCommands, other.negativeCommands, other.attackCommands);
    }

    public int getColor() {
        return this.name.getStyle().getColor().getValue();
    }

    public ItemStack getSlimeItem() {
        ItemStack stack = new ItemStack(ModElements.Items.SLIME_ITEM.get());
        SlimeInventoryItem.setStoredSlime(stack, this);
        return stack;
    }

    public ItemStack getPlort() {
        ItemStack stack = new ItemStack(ModElements.Items.PLORT.get());
        PlortItem.setStoredPlort(stack, this);
        return stack;
    }

    public SlimeBreed validate(ResourceLocation key) {
        Preconditions.checkNotNull(this.breed, "Invalid slime breed id!");
        Preconditions.checkNotNull(this.name, "Invalid slime name!");
        Preconditions.checkNotNull(this.diet, "Invalid slime diet!");
        Preconditions.checkNotNull(this.name.getStyle().getColor(), "Invalid entity name color!");
        if (this.traits != null) {
            this.traits.forEach((trait) -> {
                // Why is Java like that????
                if (!POSSIBLE_TRAITS.contains(trait.replace("\"", ""))) {
                    SplendidSlimes.LOGGER.info("Registering custom trait: " + trait);
                }
            });
        }
        return this;
    }

    @Override
    public Codec<? extends SlimeBreed> getCodec() {
        return CODEC;
    }

    public static class SlimeBreedCodec implements Codec<SlimeBreed> {

        @Override
        public <T> DataResult<T> encode(SlimeBreed input, DynamicOps<T> ops, T prefix) {
            JsonObject obj = new JsonObject();
            ResourceLocation key = new ResourceLocation(SplendidSlimes.MODID, input.breed);
            obj.addProperty("breed", input.breed);
            obj.addProperty("name", ((TranslatableContents) input.name.getContents()).getKey());
            obj.add("hat", ItemAdapter.ITEM_READER.toJsonTree(input.hat));
            obj.addProperty("hat_scale", input.hatScale);
            obj.addProperty("hat_x_offset", input.hatXOffset);
            obj.addProperty("hat_y_offset", input.hatYOffset);
            obj.addProperty("hat_z_offset", input.hatZOffset);
            obj.add("particle", ItemAdapter.ITEM_READER.toJsonTree(input.particle));
            obj.addProperty("diet", ((TranslatableContents) input.diet.getContents()).getKey());
            obj.addProperty("color", input.name.getStyle().getColor().serialize());
            JsonArray foods = new JsonArray();
            obj.add("foods", foods);
            for (Object food : input.foods) {
                if (food.getClass() == ItemStack.class) {
                    JsonElement newStack = ItemAdapter.ITEM_READER.toJsonTree(food);
                    JsonObject foodJson = newStack.getAsJsonObject();
                    ResourceLocation itemName = new ResourceLocation(foodJson.get("item").getAsString());
                    if (!"minecraft".equals(itemName.getNamespace()) && !key.getNamespace().equals(itemName.getNamespace())) {
                        foodJson.addProperty("optional", true);
                    }
                    foods.add(foodJson);
                }
                if (food.getClass() == TagKey.class) {
                    JsonObject tagJson = new JsonObject();
                    tagJson.addProperty("tag", ((TagKey<?>) food).location().toString());
                    foods.add(tagJson);
                }
            }
            obj.add("favorite_food", ItemAdapter.ITEM_READER.toJsonTree(input.favoriteFood));
            obj.add("entities", ItemAdapter.ITEM_READER.toJsonTree(input.entities.stream().map(EntityType::getKey).toList()));
            obj.addProperty("favorite_entity", EntityType.getKey(input.favoriteEntity).toString());
            obj.add("hostile_to_entities", ItemAdapter.ITEM_READER.toJsonTree(input.hostileToEntities.stream().map(EntityType::getKey).toList()));
            JsonArray traits = new JsonArray();
            obj.add("traits", traits);
            for (String trait : input.traits) {
                traits.add(trait.replace("\"", ""));
            }
            JsonArray innateEffects = new JsonArray();
            obj.add("innate_effects", innateEffects);
            for (Object effect : input.innateEffects) {
                innateEffects.add(getEffectJson(effect, true));
            }
            obj.addProperty("emit_effect_particle", getParticleTypeJson(input.emitEffectParticle));
            JsonArray positiveEmitEffects = new JsonArray();
            obj.add("positive_emit_effects", positiveEmitEffects);
            for (Object effect : input.positiveEmitEffects) {
                positiveEmitEffects.add(getEffectJson(effect, false));
            }
            JsonArray negativeEmitEffects = new JsonArray();
            obj.add("negative_emit_effects", negativeEmitEffects);
            for (Object effect : input.negativeEmitEffects) {
                negativeEmitEffects.add(getEffectJson(effect, false));
            }
            JsonArray positiveCommands = new JsonArray();
            obj.add("positive_commands", positiveCommands);
            for (String command : input.positiveCommands) {
                positiveCommands.add(command);
            }
            JsonArray negativeCommands = new JsonArray();
            obj.add("negative_commands", negativeCommands);
            for (String command : input.negativeCommands) {
                negativeCommands.add(command);
            }
            JsonArray attackCommands = new JsonArray();
            obj.add("attack_commands", attackCommands);
            for (String command : input.attackCommands) {
                attackCommands.add(command);
            }
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, obj));
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> DataResult<Pair<SlimeBreed, T>> decode(DynamicOps<T> ops, T input) {
            JsonObject obj = ops.convertTo(JsonOps.INSTANCE, input).getAsJsonObject();

            String breed = GsonHelper.getAsString(obj, "breed");
            MutableComponent name = Component.translatable(GsonHelper.getAsString(obj, "name"));

            ItemStack hat = ModElements.Items.HAT.get().getDefaultInstance();
            if (obj.has("hat")) {
                hat = ItemAdapter.ITEM_READER.fromJson(obj.get("hat"), ItemStack.class);
            }
            float hatScale = 1F;
            if (obj.has("hat_scale")) {
                hatScale = GsonHelper.getAsFloat(obj, "hat_scale");
            }
            float hatXOffset = 0;
            if (obj.has("hat_x_offset")) {
                hatXOffset = GsonHelper.getAsFloat(obj, "hat_x_offset");
            }
            float hatYOffset = -1.0F;
            if (obj.has("hat_y_offset")) {
                hatYOffset = GsonHelper.getAsFloat(obj, "hat_y_offset");
            }
            float hatZOffset = -0.05F;
            if (obj.has("hat_z_offset")) {
                hatZOffset = GsonHelper.getAsFloat(obj, "hat_z_offset");
            }
            ItemStack particle = new ItemStack(Items.AIR);
            if (obj.has("particle")) {
                particle = ItemAdapter.ITEM_READER.fromJson(obj.get("particle"), ItemStack.class);
            }
            MutableComponent diet = Component.translatable(GsonHelper.getAsString(obj, "diet"));
            if (obj.has("color")) {
                String colorStr = GsonHelper.getAsString(obj, "color");
                var color = TextColor.parseColor(colorStr);
                name = name.withStyle(Style.EMPTY.withColor(color));
            } else name.withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
            List<Object> foods = new ArrayList<>();
            if (obj.has("foods")) {
                JsonArray parsedFood = GsonHelper.getAsJsonArray(obj, "foods");
                for (JsonElement e : parsedFood) {
                    if (e.getAsJsonObject().has("item"))
                        foods.add(ItemAdapter.ITEM_READER.fromJson(e.getAsJsonObject(), ItemStack.class));
                    else if (e.getAsJsonObject().has("tag"))
                        foods.add(TagKey.create(Registries.ITEM, new ResourceLocation(e.getAsJsonObject().get("tag").getAsString())));
                }
            }
            ItemStack favoriteFood = new ItemStack(Items.AIR);
            if (obj.has("favorite_food")) {
                favoriteFood = ItemAdapter.ITEM_READER.fromJson(obj.get("favorite_food"), ItemStack.class);
            }
            List<EntityType<? extends LivingEntity>> entities = new ArrayList<>();
            if (obj.has("entities")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "entities")) {
                    EntityType<? extends LivingEntity> st = (EntityType) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(json.getAsString()));
                    if (st != EntityType.PIG || "minecraft:pig".equals(json.getAsString())) entities.add(st);
                    // Intentionally ignore invalid entries here, so that modded entities can be added without hard deps.
                }
            }
            EntityType<? extends LivingEntity> favoriteEntity = null;
            if (obj.has("favorite_entity")) {
                String favoriteEntityStr = GsonHelper.getAsString(obj, "favorite_entity");
                favoriteEntity = (EntityType) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(favoriteEntityStr));
                if (favoriteEntity == EntityType.PIG && !"minecraft:pig".equals(favoriteEntityStr))
                    throw new JsonParseException("Slime has invalid favorite entity type " + favoriteEntityStr);
            }
            List<EntityType<? extends LivingEntity>> hostileToEntitites = new ArrayList<>();
            if (obj.has("hostile_to_entities")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "hostile_to_entities")) {
                    EntityType<? extends LivingEntity> st = (EntityType) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(json.getAsString()));
                    if (st != EntityType.PIG || "minecraft:pig".equals(json.getAsString())) hostileToEntitites.add(st);
                    // Intentionally ignore invalid entries here, so that modded entities can be added without hard deps.
                }
            }
            List<String> traits = new ArrayList<>();
            if (obj.has("traits")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "traits")) {
                    traits.add(String.valueOf(json).replace("\"", ""));
                }
            }
            List<MobEffectInstance> innateEffects = new ArrayList<>();
            if (obj.has("innate_effects")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "innate_effects")) {
                    innateEffects.add(getEffectFromJson(json, true));
                }
            }
            SimpleParticleType emitEffectParticleType = ParticleTypes.EFFECT;
            if (obj.has("emit_effect_particle")) {
                emitEffectParticleType = getParticleTypeFromJson(obj.get("emit_effect_particle"));
            }
            List<MobEffectInstance> positiveEmitEffects = new ArrayList<>();
            if (obj.has("positive_emit_effects")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "positive_emit_effects")) {
                    positiveEmitEffects.add(getEffectFromJson(json, false));
                }
            }
            List<MobEffectInstance> negativeEmitEffects = new ArrayList<>();
            if (obj.has("negative_emit_effects")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "negative_emit_effects")) {
                    negativeEmitEffects.add(getEffectFromJson(json, false));
                }
            }
            List<String> positiveCommands = new ArrayList<>();
            if (obj.has("positive_commands")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "positive_commands")) {
                    positiveCommands.add(SlimeData.parseCommand(String.valueOf(json)));
                }
            }
            List<String> negativeCommands = new ArrayList<>();
            if (obj.has("negative_commands")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "negative_commands")) {
                    positiveCommands.add(SlimeData.parseCommand(String.valueOf(json)));
                }
            }
            List<String> attackCommands = new ArrayList<>();
            if (obj.has("attack_commands")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "attack_commands")) {
                    attackCommands.add(SlimeData.parseCommand(String.valueOf(json)));
                }
            }
            return DataResult.success(Pair.of(new SlimeBreed(breed, name, hat, hatScale, hatXOffset, hatYOffset, hatZOffset, particle, diet, foods, favoriteFood, entities, favoriteEntity, hostileToEntitites, traits, innateEffects, emitEffectParticleType, positiveEmitEffects, negativeEmitEffects, positiveCommands, negativeCommands, attackCommands), input));
        }

    }

}