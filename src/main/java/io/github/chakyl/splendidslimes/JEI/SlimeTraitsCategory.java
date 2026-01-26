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

public class SlimeTraitsCategory implements IRecipeCategory<TraitRecipe> {

    public static final RecipeType<TraitRecipe> TYPE = RecipeType.create(SplendidSlimes.MODID, "slime_traits", TraitRecipe.class);
    public static final ResourceLocation TEXTURES = new ResourceLocation(SplendidSlimes.MODID, "textures/jei/slime_trait.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;


    public SlimeTraitsCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURES, 0, 0, 208, 111);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModElements.Items.SLIME_CANDY.get()));
        this.name = Component.translatable("jei.splendid_slimes.category.slime_traits");
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
    public RecipeType<TraitRecipe> getRecipeType() {
        return TYPE;
    }

    public void setRecipe(IRecipeLayoutBuilder builder, TraitRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 11).addIngredient(VanillaTypes.ITEM_STACK, recipe.slime);
    }

    @Override
    public void draw(TraitRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        String slimeId = recipe.slime.getTagElement("slime").getString("id").replace("splendid_slimes:", ".");
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("slime.splendid_slimes" + slimeId)), 32, 15, 0xFF4b3658, false);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("trait.splendid_slimes." + recipe.trait + ".name")), 104, 15, 0xFF4b3658, false);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("trait.splendid_slimes." + recipe.trait + ".info").getString()), 36, 38, 144, 0xFF4b3658);

    }
}