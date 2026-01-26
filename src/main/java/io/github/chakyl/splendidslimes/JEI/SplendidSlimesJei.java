package io.github.chakyl.splendidslimes.JEI;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.recipe.PlortPressingRecipe;
import io.github.chakyl.splendidslimes.recipe.PlortRippingRecipe;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.screen.PlortPressScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;
import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeFromEgg;

@JeiPlugin
public class SplendidSlimesJei implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation(SplendidSlimes.MODID, "plugin");

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(ModElements.Items.PLORT.get(), new ModelSubtypes());
        reg.registerSubtypeInterpreter(ModElements.Items.SLIME_HEART.get(), new ModelSubtypes());
        reg.registerSubtypeInterpreter(ModElements.Items.SLIME_ITEM.get(), new ModelSubtypes());
        reg.registerSubtypeInterpreter(ModElements.Items.SPLENDID_SLIME_SPAWN_EGG.get(), new ModelSubtypes());
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

        List<PlortRippingRecipe> rippingRecipes = recipeManager.getAllRecipesFor(PlortRippingRecipe.Type.INSTANCE);
        registration.addRecipes(PlortRippingCategory.TYPE, rippingRecipes);
        List<PlortPressingRecipe> pressingRecipes = recipeManager.getAllRecipesFor(PlortPressingRecipe.Type.INSTANCE);
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
        reg.addRecipeCatalyst(new ItemStack(ModElements.Blocks.PLORT_RIPPIT.get()), PlortRippingCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(ModElements.Blocks.PLORT_PRESS.get()), PlortPressingCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(ModElements.Blocks.SLIME_INCUBATOR.get()), SlimeIncubatingCategory.TYPE);
    }

    @Override

    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(PlortPressScreen.class, 80, 26, 20, 30,
                PlortPressingCategory.TYPE);
    }

    private static class ModelSubtypes implements IIngredientSubtypeInterpreter<ItemStack> {

        @Override
        public String apply(ItemStack stack, UidContext context) {
            DynamicHolder<SlimeBreed> slimeData;
            String accessor = "slime";
            if (stack.getItem() == ModElements.Items.PLORT.get()) {
                accessor = "plort";
            }
            if (stack.getItem() == ModElements.Items.SPLENDID_SLIME_SPAWN_EGG.get()) {
                slimeData = getSlimeFromEgg(stack);
            } else {
                slimeData = getSlimeData(stack, accessor);
            }
            if (!slimeData.isBound()) return "NULL";
            return slimeData.getId().toString();
        }

    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

}