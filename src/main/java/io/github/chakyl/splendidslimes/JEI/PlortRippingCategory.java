package io.github.chakyl.splendidslimes.JEI;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.recipe.PlortRippingRecipe;
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

public class PlortRippingCategory implements IRecipeCategory<PlortRippingRecipe> {

    public static final RecipeType<PlortRippingRecipe> TYPE = RecipeType.create(SplendidSlimes.MODID, "plort_ripping", PlortRippingRecipe.class);
    public static final ResourceLocation TEXTURES = new ResourceLocation(SplendidSlimes.MODID, "textures/jei/plort_rip_jei.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public PlortRippingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURES, 0, 0, 112, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModElements.Blocks.PLORT_RIPPIT.get()));
        this.name = Component.translatable(ModElements.Blocks.PLORT_RIPPIT.get().getDescriptionId());
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
    public RecipeType<PlortRippingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PlortRippingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 19).addItemStack(recipe.getInputItem(null));
        int row = 0;
        for (int i = 0; i < recipe.getResults(null).toArray().length; i++) {
            if (i % 3 == 0) row++;
            builder.addSlot(RecipeIngredientRole.OUTPUT, 109 + ((i - (row * 3)) * 18), 1 + ((18 * (i / 3)) )).addItemStack(recipe.getResults(null).get(i));
        }
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