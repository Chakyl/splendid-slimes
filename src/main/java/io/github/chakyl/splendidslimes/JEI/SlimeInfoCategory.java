package io.github.chakyl.splendidslimes.JEI;

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

public class SlimeInfoCategory implements IRecipeCategory<PlortRecipe> {

    public static final RecipeType<PlortRecipe> TYPE = RecipeType.create(SplendidSlimes.MODID, "slime_info", PlortRecipe.class);
    public static final ResourceLocation TEXTURES = new ResourceLocation(SplendidSlimes.MODID, "textures/jei/slimepedia.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;


    public SlimeInfoCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURES, 0, 0, 232, 111);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModElements.Items.SLIME_VAC.get()));
        this.name = Component.translatable("jei.splendid_slimes.category.slime_info");
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
    public RecipeType<PlortRecipe> getRecipeType() {
        return TYPE;
    }

    public void setRecipe(IRecipeLayoutBuilder builder, PlortRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 11).addIngredient(VanillaTypes.ITEM_STACK, recipe.slime);
        int row = 0;
        for (int i = 0; i < recipe.inputs.size() && i < 9; i++) {
            if (i % 3 == 0) row++;
            builder.addSlot(RecipeIngredientRole.INPUT, 193 + ((i - (row * 3)) * 18), 41 + ((18 * (i / 3)))).addIngredients(recipe.inputs.get(i));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 205, 84).addIngredient(VanillaTypes.ITEM_STACK, recipe.output);
    }

    @Override
    public void draw(PlortRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        String[] slimeId = recipe.slime.getTagElement("slime").getString("id").split(":");
        String translationKey = slimeId[0] + "." + slimeId[1];
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("slime." + translationKey)), 32, 15, 0xFF4b3658, false);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("slime." + translationKey + ".info").getString()), 15, 38, 107, 0xFF4b3658);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.splendid_slimes.category.slime_info.diet")), 134, 22, 0xFF4b3658, false);
    }
}