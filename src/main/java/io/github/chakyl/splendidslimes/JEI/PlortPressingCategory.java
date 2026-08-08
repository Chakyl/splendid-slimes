package io.github.chakyl.splendidslimes.JEI;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.recipe.PlortPressingRecipe;
import io.github.chakyl.splendidslimes.registry.ModElements;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class PlortPressingCategory implements IRecipeCategory<PlortPressingRecipe> {

    public static final RecipeType<PlortPressingRecipe> TYPE = RecipeType.create(SplendidSlimes.MODID, "plort_pressing", PlortPressingRecipe.class);
    public static final ResourceLocation TEXTURE = loc("textures/jei/plort_press_jei.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public PlortPressingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 84, 64);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModElements.Blocks.PLORT_PRESS.get()));
        this.name = Component.translatable(ModElements.Blocks.PLORT_PRESS.get().getDescriptionId());
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
    public RecipeType<PlortPressingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PlortPressingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 1).addItemStack(recipe.getInputItem(null));
        if (!recipe.getOutputItem(null).isEmpty())
            builder.addSlot(RecipeIngredientRole.CATALYST, 17, 43).addItemStack(recipe.getOutputItem(null));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 24).addItemStack(recipe.getResultItem(null));

    }

//    @Override
//    public void draw(PlortRippingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gfx, double mouseX, double mouseY) {
//        Minecraft mc = Minecraft.getInstance();
//        long time = mc.level.getGameTime();
//        int width = Mth.ceil(36F * (this.ticks % 40 + mc.getDeltaFrameTime()) / 40);
//        gfx.blit(TEXTURES, 34, 12, 0, 30, width, 6, 256, 256);
//        if (time != this.lastTickTime) {
//            ++this.ticks;
//            this.lastTickTime = time;
//        }
//    }

}