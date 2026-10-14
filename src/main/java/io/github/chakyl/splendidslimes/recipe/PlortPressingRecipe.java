package io.github.chakyl.splendidslimes.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class PlortPressingRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient ingredient;
    private final ItemStack input;
    private final ItemStack output;
    private final ItemStack result;

    public PlortPressingRecipe(Ingredient ingredient, ItemStack input, ItemStack output, ItemStack result) {
        this.ingredient = ingredient;
        this.input = input;
        this.output = output;
        this.result = result;
    }

    @Override
    public boolean matches(SingleRecipeInput inputWrapper, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        ItemStack containerStack = inputWrapper.getItem(0);
        return greaterThanOrEquals(this.input, containerStack);
    }

    private boolean greaterThanOrEquals(ItemStack self, ItemStack other) {
        if (self.isEmpty()) return other.isEmpty();
        return !other.isEmpty()
                && self.getCount() <= other.getCount()
                && ItemStack.isSameItemSameComponents(self, other);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput inputWrapper, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output.isEmpty() ? Items.AIR.getDefaultInstance() : output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<PlortPressingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "plort_pressing";
    }

    public static class Serializer implements RecipeSerializer<PlortPressingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<PlortPressingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(PlortPressingRecipe::getIngredient),
                ItemStack.CODEC.fieldOf("input").forGetter(PlortPressingRecipe::getInput),
                ItemStack.CODEC.optionalFieldOf("output", ItemStack.EMPTY).forGetter(PlortPressingRecipe::getOutput),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)
        ).apply(instance, PlortPressingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PlortPressingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, PlortPressingRecipe::getIngredient,
                ItemStack.STREAM_CODEC, PlortPressingRecipe::getInput,
                ItemStack.OPTIONAL_STREAM_CODEC, PlortPressingRecipe::getOutput,
                ItemStack.STREAM_CODEC, r -> r.result,
                PlortPressingRecipe::new
        );

        @Override
        public MapCodec<PlortPressingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PlortPressingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
