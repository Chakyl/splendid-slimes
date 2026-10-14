package io.github.chakyl.splendidslimes.JEI;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.item.PlortItem;
import io.github.chakyl.splendidslimes.item.SlimeHeartItem;
import io.github.chakyl.splendidslimes.item.SlimeInventoryItem;
import io.github.chakyl.splendidslimes.item.SlimeSpawnEggItem;
import io.github.chakyl.splendidslimes.recipe.PlortPressingRecipe;
import io.github.chakyl.splendidslimes.recipe.PlortRippingRecipe;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.screen.PlortPressScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

@JeiPlugin
public class SplendidSlimesJei implements IModPlugin {

    public static final ResourceLocation UID = loc("plugin");

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(ModElements.Items.SLIME_ITEM.value(), new SlimeItemSubTypes());
        reg.registerSubtypeInterpreter(ModElements.Items.PLORT.value(), new PlortSubTypes());
        reg.registerSubtypeInterpreter(ModElements.Items.SLIME_HEART.value(), new SlimeHeartSubTypes());
        reg.registerSubtypeInterpreter(ModElements.Items.SPLENDID_SLIME_SPAWN_EGG.value(), new SlimeSpawnEggSubTypes());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        reg.addRecipeCategories(new PlortRippingCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new PlortPressingCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new SlimeInfoCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new SlimeTraitsCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new SlimeIncubatingCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<PlortRippingRecipe> rippingRecipes = recipeManager.getAllRecipesFor(PlortRippingRecipe.Type.INSTANCE)
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(PlortRippingCategory.TYPE, rippingRecipes);
        List<PlortPressingRecipe> pressingRecipes = recipeManager.getAllRecipesFor(PlortPressingRecipe.Type.INSTANCE)
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(PlortPressingCategory.TYPE, pressingRecipes);
        List<PlortRecipe> breedRecipes = new ArrayList<>();
        List<SlimeRecipe> incubateRecipes = new ArrayList<>();
        for (SlimeBreed breed : SlimeBreedRegistry.INSTANCE.getValues()) {
            breedRecipes.add(new PlortRecipe(breed));
            incubateRecipes.add(new SlimeRecipe(breed));
        }
        registration.addRecipes(SlimeInfoCategory.TYPE, breedRecipes);
        registration.addRecipes(SlimeIncubatingCategory.TYPE, incubateRecipes);
        List<TraitRecipe> traitRecipes = new ArrayList<>();
        for (SlimeBreed breed : SlimeBreedRegistry.INSTANCE.getValues()) {
            int traitCount = breed.traits().size();
            for (int i = 0; i < traitCount; i++) {
                traitRecipes.add(new TraitRecipe(breed, breed.traits().get(i)));
            }
        }
        registration.addRecipes(SlimeTraitsCategory.TYPE, traitRecipes);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(ModElements.Blocks.PLORT_RIPPIT.value()), PlortRippingCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(ModElements.Blocks.PLORT_PRESS.value()), PlortPressingCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(ModElements.Blocks.SLIME_INCUBATOR.value()), SlimeIncubatingCategory.TYPE);
    }

    @Override

    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(PlortPressScreen.class, 80, 26, 20, 30,
                PlortPressingCategory.TYPE);
    }
    private static class PlortSubTypes implements ISubtypeInterpreter<ItemStack> {
        @Override
        public DynamicHolder<SlimeBreed> getSubtypeData(ItemStack stack, UidContext context) {
            return PlortItem.setStoredPlort(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            DynamicHolder<SlimeBreed> slime = PlortItem.getStoredPlort(stack);
            if (!slime.isBound()) return "NULL";
            return slime.getId().toString();
        }
    }
    private static class SlimeItemSubTypes implements ISubtypeInterpreter<ItemStack> {
        @Override
        public DynamicHolder<SlimeBreed> getSubtypeData(ItemStack stack, UidContext context) {
            return SlimeInventoryItem.setStoredSlime(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            DynamicHolder<SlimeBreed> slime = SlimeInventoryItem.getStoredSlime(stack);
            if (!slime.isBound()) return "NULL";
            return slime.getId().toString();
        }
    }
    private static class SlimeHeartSubTypes implements ISubtypeInterpreter<ItemStack> {
        @Override
        public DynamicHolder<SlimeBreed> getSubtypeData(ItemStack stack, UidContext context) {
            return SlimeHeartItem.setStoredPlort(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            DynamicHolder<SlimeBreed> slime = SlimeHeartItem.getStoredPlort(stack);
            if (!slime.isBound()) return "NULL";
            return slime.getId().toString();
        }
    }
    private static class SlimeSpawnEggSubTypes implements ISubtypeInterpreter<ItemStack> {
        @Override
        public DynamicHolder<SlimeBreed> getSubtypeData(ItemStack stack, UidContext context) {
            return SlimeSpawnEggItem.setStoredSlime(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            DynamicHolder<SlimeBreed> slime = SlimeSpawnEggItem.getStoredSlime(stack);
            if (!slime.isBound()) return "NULL";
            return slime.getId().toString();
        }
    }
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

}