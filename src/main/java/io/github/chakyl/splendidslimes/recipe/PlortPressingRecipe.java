package io.github.chakyl.splendidslimes.recipe;

import com.google.gson.JsonObject;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class PlortPressingRecipe implements Recipe<SimpleContainer> {
    private final Ingredient ingredient;
    private final ItemStack input;
    private final ItemStack output;
    private final ItemStack result;
    private final ResourceLocation id;

    public PlortPressingRecipe(Ingredient ingredient, ItemStack input, ItemStack output, ItemStack result, ResourceLocation id) {
        this.ingredient = ingredient;
        this.input = input;
        this.output = output;
        this.result = result;
        this.id = id;
    }


    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        boolean outputMatches = true;
        if (this.output != Items.AIR.getDefaultInstance() && this.output != null && !output.isEmpty())
            outputMatches = greaterThanOrEquals(output, pContainer.getItem(1));
        return outputMatches && greaterThanOrEquals(input, pContainer.getItem(0));
    }

    private boolean greaterThanOrEquals(ItemStack self, ItemStack other) {
        if (self.isEmpty()) return other.isEmpty();
        else
            return !other.isEmpty() && self.getCount() <= other.getCount() && self.getItem() == other.getItem() && self.areShareTagsEqual(other);
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public ItemStack getInputItem(RegistryAccess pRegistryAccess) {
        return input.copy();
    }

    public ItemStack getOutputItem(RegistryAccess pRegistryAccess) {
        if (output != null) return output.copy();
        return Items.AIR.getDefaultInstance();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModElements.Recipes.PLORT_PRESSING_SERIALIZER.get();
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
        public static final ResourceLocation ID = loc("plort_pressing");

        @Override
        public PlortPressingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
            ItemStack outputItem = Items.AIR.getDefaultInstance();
            if (pSerializedRecipe.has("output")) {
                outputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            }

            ItemStack inputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            Ingredient ingredientItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));

            return new PlortPressingRecipe(ingredientItem, inputItem, outputItem, result, pRecipeId);
        }

        @Override
        public @Nullable PlortPressingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient recipeIngredient = Ingredient.fromNetwork(pBuffer);
            ItemStack input = pBuffer.readItem();
            ItemStack output = pBuffer.readItem();
            ItemStack result = pBuffer.readItem();
            return new PlortPressingRecipe(recipeIngredient, input, output, result, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, PlortPressingRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);

            pBuffer.writeItemStack(pRecipe.getInputItem(null), false);
            pBuffer.writeItemStack(pRecipe.getOutputItem(null), false);
            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}
