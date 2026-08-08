package io.github.chakyl.splendidslimes.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.NonNullList;
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

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class PlortRippingRecipe implements Recipe<SimpleContainer> {
    private final Ingredient ingredient;
    private final ItemStack input;
    private final NonNullList<ItemStack> results;
    private final List<Integer> weights;
    private final ResourceLocation id;

    public PlortRippingRecipe(Ingredient ingredient, ItemStack input, NonNullList<ItemStack> results, List<Integer> weights, ResourceLocation id) {
        this.ingredient = ingredient;
        this.input = input;
        this.results = results;
        this.weights = weights;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        return input.equals(pContainer.getItem(0), true);
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return results.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public ItemStack getInputItem(RegistryAccess pRegistryAccess) {
        return input.copy();
    }

    public NonNullList<ItemStack> getResults(RegistryAccess pRegistryAccess) {
        return results;
    }
    public List<Integer> getWeights(RegistryAccess pRegistryAccess) {
        return weights;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return results.get(0).copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModElements.Recipes.PLORT_RIPPING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<PlortRippingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "plort_ripping";
    }

    public static class Serializer implements RecipeSerializer<PlortRippingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = loc("plort_ripping");

        @Override
        public PlortRippingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonArray resultJson = GsonHelper.getAsJsonArray(pSerializedRecipe, "results");
            NonNullList<ItemStack> results = NonNullList.withSize(resultJson.size(), Items.AIR.getDefaultInstance());
            List<Integer> weights = new ArrayList<>();

            for (int i = 0; i < resultJson.size(); i++) {
                JsonObject itemStackJson = new JsonObject();
                JsonObject thisJson = resultJson.get(i).getAsJsonObject();
                itemStackJson.add("item", thisJson.get("item"));
                itemStackJson.add("count", thisJson.get("count"));
                if (thisJson.has("nbt")) itemStackJson.add("nbt", thisJson.get("nbt"));
                results.set(i,ShapedRecipe.itemStackFromJson(itemStackJson));
                if (thisJson.has("weight")) {
                    weights.add(thisJson.get("weight").getAsInt());
                } else {
                    weights.add(1);
                }
            }
            ItemStack inputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            Ingredient ingredientItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));


            return new PlortRippingRecipe(ingredientItem, inputItem, results, weights, pRecipeId);
        }

        @Override
        public @Nullable PlortRippingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient recipeIngredient =  Ingredient.fromNetwork(pBuffer);
            ItemStack input = pBuffer.readItem();
            NonNullList<ItemStack> results = NonNullList.withSize(pBuffer.readVarInt(), Items.AIR.getDefaultInstance());

            List<Integer> weights = new ArrayList<>();

            for (int i = 0; i < results.size(); i++) {
                results.set(i, pBuffer.readItem());
            }
            for (int i = 0; i < results.size(); i++) {
                weights.add(pBuffer.readVarInt());
            }

            return new PlortRippingRecipe(recipeIngredient, input, results, weights, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, PlortRippingRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItemStack(pRecipe.getInputItem(null), false);
            pBuffer.writeVarInt(pRecipe.getResults(null).size());

            for (ItemStack result : pRecipe.getResults(null)) {
                pBuffer.writeItemStack(result, false);
            }
            for (Integer weight : pRecipe.getWeights(null)) {
                pBuffer.writeVarInt(weight);
            }
        }
    }
}
