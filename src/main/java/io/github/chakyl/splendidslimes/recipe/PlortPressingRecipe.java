package io.github.chakyl.splendidslimes.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.shadowsoffire.placebo.json.ItemAdapter;
import io.github.chakyl.splendidslimes.SplendidSlimes;
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

public class PlortPressingRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack input;
    private final ItemStack output;
    private final ItemStack result;
    private final ResourceLocation id;

    public PlortPressingRecipe(NonNullList<Ingredient> inputItems, ItemStack input, ItemStack output, ItemStack result, ResourceLocation id) {
        this.inputItems = inputItems;
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

        return input.equals(pContainer.getItem(0), true);
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
        public static final ResourceLocation ID = new ResourceLocation(SplendidSlimes.MODID, "plort_pressing");

        @Override
        public PlortPressingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }
            ItemStack inputItem = ItemAdapter.ITEM_READER.fromJson(ingredients.get(0), ItemStack.class);
            ItemStack outputItem = null;
            if (pSerializedRecipe.has("output")) {
                outputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            }

            return new PlortPressingRecipe(inputs, inputItem, outputItem, result, pRecipeId);
        }

        @Override
        public @Nullable PlortPressingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack input = pBuffer.readItem();
            ItemStack output = pBuffer.readItem();
            ItemStack result = pBuffer.readItem();
            return new PlortPressingRecipe(inputs, input, output, result, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, PlortPressingRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputItems.size());

            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItemStack(pRecipe.getInputItem(null), false);
            pBuffer.writeItemStack(pRecipe.getOutputItem(null), false);
            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}
