package io.github.chakyl.splendidslimes.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PlortRippingRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient ingredient;
    private final ItemStack input;
    private final NonNullList<ItemStack> results;
    private final List<Integer> weights;

    public PlortRippingRecipe(Ingredient ingredient, ItemStack input, List<WeightedOutput> outputEntries) {
        this.ingredient = ingredient;
        this.input = input;
        this.results = NonNullList.create();
        this.weights = new ArrayList<>();

        for (WeightedOutput entry : outputEntries) {
            this.results.add(entry.stack());
            this.weights.add(entry.weight());
        }
    }

    public PlortRippingRecipe(Ingredient ingredient, ItemStack input, NonNullList<ItemStack> results, List<Integer> weights) {
        this.ingredient = ingredient;
        this.input = input;
        this.results = results;
        this.weights = weights;
    }

    @Override
    public boolean matches(SingleRecipeInput singleRecipeInput, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(this.input, singleRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput pSingleRecipeInput, HolderLookup.Provider pProvider) {
        return results.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getInput() {
        return input;
    }

    public NonNullList<ItemStack> getResults() {
        return results;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public List<WeightedOutput> getOutputEntries() {
        List<WeightedOutput> entries = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            int weight = (i < weights.size()) ? weights.get(i) : 1;
            entries.add(new WeightedOutput(results.get(i), weight));
        }
        return entries;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0).copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public record WeightedOutput(ItemStack stack, int weight) {
        public static final Codec<WeightedOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("item").forGetter(WeightedOutput::stack),
                Codec.INT.optionalFieldOf("weight", 1).forGetter(WeightedOutput::weight)
        ).apply(instance, WeightedOutput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WeightedOutput> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, WeightedOutput::stack,
                ByteBufCodecs.VAR_INT, WeightedOutput::weight,
                WeightedOutput::new
        );
    }

    public static class Type implements RecipeType<PlortRippingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "plort_ripping";
    }

    public static class Serializer implements RecipeSerializer<PlortRippingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<PlortRippingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(PlortRippingRecipe::getIngredient),
                ItemStack.CODEC.fieldOf("input").forGetter(PlortRippingRecipe::getInput),
                WeightedOutput.CODEC.listOf().fieldOf("results").forGetter(PlortRippingRecipe::getOutputEntries)
        ).apply(instance, PlortRippingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PlortRippingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, PlortRippingRecipe::getIngredient,
                ItemStack.STREAM_CODEC, PlortRippingRecipe::getInput,
                WeightedOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), PlortRippingRecipe::getOutputEntries,
                PlortRippingRecipe::new
        );

        @Override
        public MapCodec<PlortRippingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PlortRippingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}