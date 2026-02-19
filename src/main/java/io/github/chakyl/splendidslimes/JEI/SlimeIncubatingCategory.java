package io.github.chakyl.splendidslimes.JEI;

import io.github.chakyl.splendidslimes.SlimyConfig;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.registry.ModElements;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SlimeIncubatingCategory implements IRecipeCategory<SlimeRecipe> {

    public static final RecipeType<SlimeRecipe> TYPE = RecipeType.create(SplendidSlimes.MODID, "slime_incubating", SlimeRecipe.class);
    public static final ResourceLocation TEXTURES = new ResourceLocation(SplendidSlimes.MODID, "textures/jei/slime_incubating_jei.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public SlimeIncubatingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURES, 0, 0, 126, 39);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModElements.Blocks.SLIME_INCUBATOR.get()));
        this.name = Component.translatable("jei.splendid_slimes.category.slime_incubating");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }


    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public Component getTitle() {
        return this.name;
    }

    @Override
    public RecipeType<SlimeRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlimeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5).addIngredient(VanillaTypes.ITEM_STACK, recipe.heart);
        builder.addSlot(RecipeIngredientRole.CATALYST, 54, 5).addItemStack(ModElements.Items.SLIME_INCUBATOR.get().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 5).addIngredient(VanillaTypes.ITEM_STACK, recipe.slime);
    }

    @Override
    public void draw(SlimeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.splendid_slimes.category.slime_incubation.incubation_time", SlimyConfig.incubationTime / 20)), 4, 28, 0xFF4b3658, false);
    }
}