package io.github.chakyl.splendidslimes.screen;

import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class SlimepediaScreen extends Screen {
    private static final ResourceLocation GUI_LOCATION = loc("textures/slimepedia/slimepedia.png");
    private static final int MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS = 20;
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(GUI_LOCATION, GUI_LOCATION);
    private static final int BREED_BUTTON_ROWS = 4;
    private static final int MENU_BUTTON_HEIGHT = 24;
    private static final int MENU_BUTTON_WIDTH = 87;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 2;
    private static final int SCROLL_BAR_HEIGHT = MENU_BUTTON_HEIGHT * MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS;
    private static final int SCROLL_BAR_TOP_POS_Y = 5;
    private static final int SCROLL_BAR_START_X = 185;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    List<SlimeBreed> breeds;
    private final List<String> discoveredBreeds;
    protected int imageWidth = 223;
    protected int imageHeight = 143;
    private boolean isDragging;
    protected int leftPos;
    protected int topPos;
    private int scrollOff = 0;
    private SlimeBreed selectedBreed;
    private SlimepediaState selectedState = SlimepediaState.INFO;

    public SlimepediaScreen(List<String> discoveredBreeds) {
        super(Component.translatable("gui.scp.containment_unit_pda.title"));
        this.discoveredBreeds = discoveredBreeds;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.breeds = SlimeBreedRegistry.INSTANCE.getValues().stream().toList();
        rebuildWidgets();
    }

    public void rebuildWidgets() {
        this.clearWidgets();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        int k = this.topPos + 23;
        int l = this.leftPos + 93;
        int row = 0;
        for (int i = 0; i < this.breeds.size() && i < MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS; i++) {
            if (i % BREED_BUTTON_ROWS == 0) row++;
//            if (i >= discoveredBreeds.size()) break;
            SlimeBreed breed = breeds.get(i);
            int slotIndex = i;
            this.addRenderableWidget(new SlimeBreedButton(l + ((i - (row * BREED_BUTTON_ROWS)) * 21), k + ((21 * (i / BREED_BUTTON_ROWS))), i, 18, 18, breed.getPlort(), BUTTON_SPRITES, (button) -> {

                int itemIndex = this.breeds.size() > BREED_BUTTON_ROWS ? this.scrollOff + slotIndex : slotIndex;
                if (itemIndex < this.breeds.size()) {
                    selectedBreed = breed;
                }
            }));
        }
        this.addRenderableWidget(new SlimeInfoButton(l + 88, k - 4, 10, 10, BUTTON_SPRITES, (button) -> {
            selectedState = SlimepediaState.INFO;
        }));
        this.addRenderableWidget(new SlimeDietButton(l + 100, k - 4, 10, 10, BUTTON_SPRITES, (button) -> {
            selectedState = SlimepediaState.DIET;
        }));
        this.addRenderableWidget(new SlimeTraitButton(l  + 112, k - 4, 10, 10, BUTTON_SPRITES, (button) -> {
            selectedState = SlimepediaState.TRAITS;
        }));

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        this.renderBg(guiGraphics, partialTick, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        guiGraphics.drawString(this.font, Component.literal("Slime Breeds"), this.leftPos + 11, this.topPos + 11, 0x5088a4, false);
        if (this.selectedBreed != null) {
            guiGraphics.drawString(this.font, this.selectedBreed.name(), this.leftPos + 116, this.topPos + 16, 0x5088a4, false);
            guiGraphics.renderFakeItem(this.selectedBreed.getSlimeItem(), this.leftPos + 96, this.topPos + 11);
            if (this.selectedState == SlimepediaState.INFO) {
                ItemStack foodIcon = Items.BOWL.getDefaultInstance();
                if (!this.selectedBreed.foods().isEmpty()){
                    if (this.selectedBreed.foods().getFirst() instanceof Ingredient ingredient) {
                        if (ingredient.getItems().length > 0) {
                            foodIcon = ingredient.getItems()[0];
                        }
                    }
                }
                guiGraphics.renderFakeItem(foodIcon, this.leftPos + 96, this.topPos + 34);
                guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("entity.splendid_slimes.diet", this.selectedBreed.diet()).getString()), this.leftPos + 114, this.topPos + 38, 120, 0x5088a4);
                guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("slime.splendid_slimes." + this.selectedBreed.breed() + ".info").getString()), this.leftPos + 99, this.topPos + 58, 114, 0x5088a4);
            }
        } else {
            guiGraphics.drawString(this.font, Component.literal("Slimepedia"), this.leftPos + 128, this.topPos + 52, 0x5088a4, false);
            guiGraphics.drawString(this.font, Component.literal(this.discoveredBreeds.size() + "/"+ this.breeds.size() + " Discovered"), this.leftPos + 114, this.topPos + 76, 0x5088a4, false);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        int i = this.breeds.size();
        if (this.canScroll(i)) {
            int j = i - MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS;
            this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - pScrollY), 0, j);
        }

        return true;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int i = this.breeds.size();
        if (this.isDragging) {
            int j = this.topPos + SCROLL_BAR_TOP_POS_Y;
            int k = j + SCROLL_BAR_HEIGHT;
            int l = i - MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS;
            float f = ((float) pMouseY - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
            f = f * (float) l + 0.5F;
            this.scrollOff = Mth.clamp((int) f, 0, l);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(this.discoveredBreeds.size()) && pMouseX > (double) (i + SCROLL_BAR_START_X) && pMouseX < (double) (i + SCROLL_BAR_START_X + 6) && pMouseY > (double) (j + SCROLL_BAR_TOP_POS_Y) && pMouseY <= (double) (j + SCROLL_BAR_TOP_POS_Y + SCROLL_BAR_HEIGHT + 1)) {
            this.isDragging = true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void renderScroller(GuiGraphics pGuiGraphics) {
        int pPosX = this.leftPos;
        int pPosY = this.topPos;
        int i = this.discoveredBreeds.size() + 1 - MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS;
        if (i > 1) {
            int j = SCROLL_BAR_HEIGHT - (SCROLLER_HEIGHT + (i - 1) * SCROLL_BAR_HEIGHT / i);
            int k = j / i + SCROLL_BAR_HEIGHT / i;
            int l = SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT;
            int i1 = Math.min(l, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = l;
            }
            pGuiGraphics.blit(GUI_LOCATION, pPosX + SCROLL_BAR_START_X, pPosY + SCROLL_BAR_TOP_POS_Y + i1, 0, 192.0F, 0.0F, 6, SCROLLER_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            pGuiGraphics.blit(GUI_LOCATION, pPosX + SCROLL_BAR_START_X, pPosY + SCROLL_BAR_TOP_POS_Y, 0, 198.0F, 0.0F, 6, SCROLLER_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

    }

    private boolean canScroll(int pNumOffers) {
        return pNumOffers > MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    class SlimeBreedButton extends ImageButton {
        final int index;
        final ItemStack icon;

        public SlimeBreedButton(int pX, int pY, int pIndex, int pWidth, int pHeight, ItemStack icon, WidgetSprites pWidgetSprites, Button.OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
            this.index = pIndex;
            this.icon = icon;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 224;
            int v = 48;

            if (!this.active) {
                v += 18;

            } else if (this.isHoveredOrFocused()) {
                v += 18;
            }

            guiGraphics.renderFakeItem(icon, this.getX() + 1, this.getY() + 1);
            guiGraphics.blit(GUI_LOCATION, this.getX(), this.getY(), u, v, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class SlimeInfoButton extends ImageButton {

        public SlimeInfoButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, Button.OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 224;
            int v = 16;

            if (!this.active) {
                v += 10;

            } else if (this.isHoveredOrFocused()) {
                v += 10;
            }

            guiGraphics.blit(GUI_LOCATION, this.getX(), this.getY(), u, v, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class SlimeDietButton extends ImageButton {

        public SlimeDietButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, Button.OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 234;
            int v = 16;

            if (!this.active) {
                v += 10;

            } else if (this.isHoveredOrFocused()) {
                v += 10;
            }

            guiGraphics.blit(GUI_LOCATION, this.getX(), this.getY(), u, v, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class SlimeTraitButton extends ImageButton {

        public SlimeTraitButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, Button.OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
        }


        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 244;
            int v = 16;

            if (!this.active) {
                v += 10;

            } else if (this.isHoveredOrFocused()) {
                v += 10;
            }

            guiGraphics.blit(GUI_LOCATION, this.getX(), this.getY(), u, v, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    public enum SlimepediaState {
        INFO,
        DIET,
        TRAITS
    }
}