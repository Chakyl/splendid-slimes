package io.github.chakyl.splendidslimes.datagen;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PlortPressRecipeBuilder {
    private final String breed;

    private PlortPressRecipeBuilder(String breed) {
        this.breed = breed;
    }

    public static PlortPressRecipeBuilder pressRecipe(String breed) {
        return new PlortPressRecipeBuilder(breed);
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        this.build(consumerIn, SplendidSlimes.MODID + ":plort_pressing/" + this.breed);
    }
    public void build(Consumer<FinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = ModElements.Items.SLIME_HEART.getId();
        if ((ResourceLocation.parse(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Cooking Recipe " + save + " should remove its 'save' argument");
        } else {
            build(consumerIn, ResourceLocation.parse(save));
        }
    }
    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
        consumerIn.accept(new PlortPressRecipeBuilder.Result(id, this.breed));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String breed;
        private final int inputCount = 32;

        public Result(ResourceLocation idIn, String breed) {
            this.id = idIn;
            this.breed = breed;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray arrayIngredients = new JsonArray();
            // Input
            JsonObject inputIngredient = new JsonObject();
            JsonObject inputNbt = new JsonObject();
            JsonObject plortId = new JsonObject();
            plortId.addProperty("id", SplendidSlimes.MODID + ":" + this.breed);
            inputNbt.add("plort", plortId);
            inputIngredient.addProperty("item", SplendidSlimes.MODID + ":plort");
            inputIngredient.add("nbt", inputNbt);
            inputIngredient.addProperty("count", this.inputCount);
            json.add("ingredient", inputIngredient);

            // Result
            JsonObject result = new JsonObject();

            JsonObject outputNbt = new JsonObject();
            JsonObject slimeId = new JsonObject();
            slimeId.addProperty("id", SplendidSlimes.MODID + ":" + this.breed);
            outputNbt.add("slime", slimeId);
            result.addProperty("item", SplendidSlimes.MODID + ":slime_heart");
            result.add("nbt", outputNbt);

            json.add("result", result);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModElements.Recipes.PLORT_PRESSING_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}