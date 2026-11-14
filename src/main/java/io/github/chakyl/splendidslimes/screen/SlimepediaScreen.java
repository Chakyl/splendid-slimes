package io.github.chakyl.splendidslimes.screen;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.entity.slimeffects.SlimeEffect;
import io.github.chakyl.splendidslimes.entity.slimeffects.SlimeEffectType;
import io.github.chakyl.splendidslimes.entity.slimetraits.SlimeTrait;
import io.github.chakyl.splendidslimes.item.PlortItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.chakyl.splendidslimes.SplendidSlimes.MODID;
import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class SlimepediaScreen extends Screen {
    private static final ResourceLocation GUI_LOCATION = loc("textures/slimepedia/slimepedia.png");
    private static final int MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS = 16;
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(GUI_LOCATION, GUI_LOCATION);
    private static final int BREED_BUTTON_ROWS = 4;
    private static final int MENU_BUTTON_HEIGHT = 24;
    private static final int MENU_BUTTON_WIDTH = 87;
    private static final int SCROLLER_HEIGHT = 14;
    private static final int SCROLLER_WIDTH = 2;
    private static final int SCROLL_BAR_HEIGHT = MENU_BUTTON_HEIGHT * MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS;
    private static final int SCROLL_BAR_TOP_POS_Y = 31;
    private static final int SCROLL_BAR_START_X = 216;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int TRAITS_TAB_X = 94;
    private static final int TRAITS_TAB_Y = 32;
    private static final int TRAITS_TAB_WIDTH = 120;
    private static final int TRAITS_TAB_HEIGHT = 96;
    List<SlimeBreed> breeds;
    private final List<String> discoveredBreeds;
    protected int imageWidth = 223;
    protected int imageHeight = 143;
    private boolean isDragging;
    protected int leftPos;
    protected int topPos;
    private int scrollOff = 0;
    private int currentPage = 1;
    private SlimeBreed selectedBreed;
    private SlimepediaState selectedState = SlimepediaState.INFO;

    public SlimepediaScreen(List<String> discoveredBreeds) {
        super(Component.translatable("gui.splendid_slimes.slimepedia.name"));
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
        for (int i = (MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS * (currentPage - 1)); i < this.breeds.size() && i < MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS * currentPage; i++) {
            if (i % BREED_BUTTON_ROWS == 0) row++;
            SlimeBreed breed = breeds.get(i);
            boolean discovered = this.discoveredBreeds.contains(MODID + ":" + breed.breed());
            int positionI = i < MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS ? i : i - (MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS * (currentPage - 1));
            this.addRenderableWidget(new SlimeBreedButton(l + ((positionI - (row * BREED_BUTTON_ROWS)) * 21), k + ((21 * (positionI / BREED_BUTTON_ROWS))), positionI, 18, 18, discovered ? breed.getPlort() : ModElements.Items.PLORT.value().getDefaultInstance(), BUTTON_SPRITES, (button) -> {
                if (discovered) {
                    this.scrollOff = 0;
                    selectedBreed = breed;
                }
            }));
        }

        // Info pages
        this.addRenderableWidget(new SlimeInfoButton(l + 88, k - 4, 10, 10, BUTTON_SPRITES, (button) -> {
            selectedState = SlimepediaState.INFO;
        }));
        this.addRenderableWidget(new SlimeDietButton(l + 100, k - 4, 10, 10, BUTTON_SPRITES, (button) -> {
            selectedState = SlimepediaState.DIET;
        }));
        this.addRenderableWidget(new SlimeTraitButton(l + 112, k - 4, 10, 10, BUTTON_SPRITES, (button) -> {
            this.scrollOff = 0;
            selectedState = SlimepediaState.TRAITS;
        }));

        // Next/Prev
        if (this.breeds.size() - MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS > 0) {
            if (currentPage > 1) {
                this.addRenderableWidget(new PrevButton(l - 82, k + 86, 16, 16, BUTTON_SPRITES, (button) -> {
                    currentPage--;
                    rebuildWidgets();
                }));
            }
            if (this.breeds.size() - (MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS * currentPage) > 1) {
                this.addRenderableWidget(new NextButton(l - 22, k + 86, 16, 16, BUTTON_SPRITES, (button) -> {
                    currentPage++;
                    rebuildWidgets();
                }));
            }
        }
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
        guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.slime_breeds"), this.leftPos + 11, this.topPos + 10, 0x5088a4, false);
        if (this.selectedBreed != null) {
            guiGraphics.drawString(this.font, this.selectedBreed.name(), this.leftPos + 116, this.topPos + 16, 0x5088a4, false);
            guiGraphics.renderFakeItem(this.selectedBreed.getSlimeItem(), this.leftPos + 96, this.topPos + 11);
            if (this.selectedState == SlimepediaState.INFO) {
                renderInfoTab(guiGraphics);
            } else if (this.selectedState == SlimepediaState.DIET) {
                renderDietTab(guiGraphics, mouseX, mouseY);
            } else if (this.selectedState == SlimepediaState.TRAITS) {
                renderTraitsTab(guiGraphics);
            }
        } else {
            guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.name"), this.leftPos + 128, this.topPos + 52, 0x5088a4, false);
            guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.discovered", this.discoveredBreeds.size() + "/" + this.breeds.size()), this.leftPos + 114, this.topPos + 76, 0x5088a4, false);
        }
        if (this.breeds.size() - MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS >= 1) {
            guiGraphics.drawString(this.font, Component.literal(this.currentPage + "/" + (int) Math.ceil((double) this.breeds.size() / MAX_VISIBLE_SLIME_BREED_BUTTONS_BUTTONS)), this.leftPos + 41, this.topPos + 113, 0x5088a4, false);
        }
    }

    private void renderInfoTab(GuiGraphics guiGraphics) {
        ItemStack foodIcon = Items.BOWL.getDefaultInstance();
        if (!this.selectedBreed.foods().isEmpty()) {
            if (this.selectedBreed.foods().getFirst() instanceof Ingredient ingredient) {
                if (ingredient.getItems().length > 0) {
                    foodIcon = ingredient.getItems()[0];
                }
            }
        }
        guiGraphics.renderFakeItem(foodIcon, this.leftPos + 96, this.topPos + 34);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, this.selectedBreed.diet(), this.leftPos + 116, this.topPos + 38, 120, 0x5088a4);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("slime.splendid_slimes." + this.selectedBreed.breed() + ".info").getString()), this.leftPos + 99, this.topPos + 58, 114, 0x5088a4);
    }

    private void renderDietTab(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_LOCATION, this.leftPos + 96, this.topPos + 38, 0, 192, 144, 8, 8, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        ItemStack hoveredItem = ItemStack.EMPTY;
        Ingredient hoveredIngredient = Ingredient.EMPTY;
        boolean hoveredMobTooltip = false;
        if (!this.selectedBreed.favoriteFood().isEmpty()) {
            int xPos = this.leftPos + 106;
            int yPos = this.topPos + 34;
            guiGraphics.renderFakeItem(this.selectedBreed.favoriteFood(), xPos, yPos);
            if (mouseX >= xPos && mouseX < xPos + 16 && mouseY >= yPos && mouseY < yPos + 16) {
                hoveredItem = this.selectedBreed.favoriteFood();
            }
            guiGraphics.drawWordWrap(Minecraft.getInstance().font, this.selectedBreed.favoriteFood().getHoverName(), this.leftPos + 126, this.topPos + 38, 120, 0x5088a4);
        } else if (this.selectedBreed.favoriteEntity().isPresent()) {
            guiGraphics.drawWordWrap(Minecraft.getInstance().font, this.selectedBreed.favoriteEntity().get().getDescription(), this.leftPos + 108, this.topPos + 38, 120, 0x5088a4);
        }
        if (!this.selectedBreed.foods().isEmpty()) {
            boolean hasItemFoods = false;
            int row = 0;
            for (int i = 0; i < this.selectedBreed.foods().size() && i < 15; i++) {
                if (i % 5 == 0) row++;
                int xPos = this.leftPos + 190 + ((i - (row * 5)) * 18);
                int yPos = this.topPos + 54 + ((18 * (i / 5)));
                ItemStack foodIcon = Items.BOWL.getDefaultInstance();
                boolean renderTagIcon = false;
                Ingredient currentIngredient = null;
                if (this.selectedBreed.foods().get(i) instanceof Ingredient ingredient) {
                    currentIngredient = ingredient;
                    if (ingredient.getItems().length > 0) {
                        foodIcon = ingredient.getItems()[0];
                        if (foodIcon.getItem() instanceof SpawnEggItem) {
                            continue;
                        }
                        hasItemFoods = true;
                        if (ingredient.getItems().length > 1) {
                            renderTagIcon = true;
                        }
                    }
                }
                guiGraphics.renderFakeItem(foodIcon, xPos, yPos);
                if (mouseX >= xPos && mouseX < xPos + 16 && mouseY >= yPos && mouseY < yPos + 16) {
                    hoveredItem = foodIcon;
                    hoveredIngredient = currentIngredient;
                }
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                if (renderTagIcon)
                    guiGraphics.blit(GUI_LOCATION, xPos + 10, yPos, 0, 208, 144, 8, 8, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                guiGraphics.pose().popPose();
            }
            if (!hasItemFoods) {
                guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.no_items"), this.leftPos + 96, this.topPos + 58, 0x5088a4, false);
            }
            if (!this.selectedBreed.entities().isEmpty()) {
                int yPos = ((row + 1) * 22);
                guiGraphics.renderFakeItem(Items.IRON_SWORD.getDefaultInstance(), this.leftPos + 96, this.topPos + 30 + yPos);
                guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.eats_these_mobs"), this.leftPos + 116, this.topPos + 34 + yPos, 0x5088a4, false);
                guiGraphics.blit(GUI_LOCATION, this.leftPos + 202, this.topPos + 33 + yPos, 0, 176, 144, 10, 10, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                if (mouseX >= this.leftPos + 202 - 4 && mouseX < this.leftPos + 202 + 16 && mouseY >= this.topPos + 33 + yPos - 4 && mouseY < this.topPos + 33 + yPos + 16) {
                    hoveredMobTooltip = true;
                }
            }
        }
        int maxToShow = 10;
        if (hoveredMobTooltip) {
            List<Component> tooltipLines = new ArrayList<>(getTooltipFromItem(this.minecraft, Items.ACACIA_BOAT.getDefaultInstance()));
            if (!this.selectedBreed.entities().isEmpty()) {
                tooltipLines.clear();
                tooltipLines.add(Component.translatable("gui.splendid_slimes.slimepedia.eats_these_mobs").withStyle(ChatFormatting.GRAY));
                int limit = Math.min(this.selectedBreed.entities().size(), maxToShow);
                for (int j = 0; j < limit; j++) {
                    tooltipLines.add(Component.literal(" - ").append(this.selectedBreed.entities().get(j).getDescription()).withStyle(ChatFormatting.DARK_GRAY));
                }
                if (this.selectedBreed.entities().size() > maxToShow) {
                    tooltipLines.add(Component.translatable("gui.splendid_slimes.slimepedia.other_similar_mobs", this.selectedBreed.entities().size() - maxToShow).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
                }
                guiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), mouseX, mouseY);
            }
        } else if (!hoveredItem.isEmpty()) {
            List<Component> tooltipLines = new ArrayList<>(getTooltipFromItem(this.minecraft, hoveredItem));
            if (!hoveredIngredient.isEmpty() && hoveredIngredient.getItems().length > 1) {
                tooltipLines.clear();
                tooltipLines.add(Component.translatable("gui.splendid_slimes.slimepedia.tag_eats").withStyle(ChatFormatting.GRAY));
                ItemStack[] tagItems = hoveredIngredient.getItems();
                int limit = Math.min(tagItems.length, maxToShow);
                for (int j = 0; j < limit; j++) {
                    tooltipLines.add(Component.literal(" - ").append(tagItems[j].getHoverName()).withStyle(ChatFormatting.DARK_GRAY));
                }
                if (tagItems.length > maxToShow) {
                    tooltipLines.add(Component.translatable("gui.splendid_slimes.slimepedia.other_similar_items", tagItems.length - maxToShow).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
                }
            }

            guiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), mouseX, mouseY);
        }
    }

    private void renderTraitsTab(GuiGraphics guiGraphics) {
        int startX = this.leftPos + TRAITS_TAB_X;
        int startY = this.topPos + TRAITS_TAB_Y;

        guiGraphics.enableScissor(startX, startY, startX + TRAITS_TAB_WIDTH + 8, startY + TRAITS_TAB_HEIGHT);

        int topPosOffset = startY - this.scrollOff;
        guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.traits").withStyle(ChatFormatting.UNDERLINE), this.leftPos + 96, topPosOffset, 0x5088a4, false);
        topPosOffset += 14;
        if (this.selectedBreed.traits().isEmpty()) {
            guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.no_traits"), this.leftPos + 96, topPosOffset, 0x5088a4, false);
            topPosOffset += 14;
        } else {
            for (SlimeTrait trait : this.selectedBreed.traits()) {
                guiGraphics.drawString(this.font, Component.translatable("trait.splendid_slimes." + trait.getSerializedName() + ".name"), this.leftPos + 96, topPosOffset, 0x5088a4, false);
                Component traitDescription = Component.translatable("trait.splendid_slimes." + trait.getSerializedName() + ".info").withStyle(ChatFormatting.ITALIC);
                guiGraphics.drawWordWrap(this.font, traitDescription, this.leftPos + 96, topPosOffset + 12, TRAITS_TAB_WIDTH, 0x5088a4);

                topPosOffset += (11 * (int) Math.ceil((double) this.font.width(traitDescription) / TRAITS_TAB_WIDTH)) + 14;
            }
        }
        topPosOffset += 14;
        guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.effects").withStyle(ChatFormatting.UNDERLINE), this.leftPos + 96, topPosOffset, 0x5088a4, false);
        topPosOffset += 14;
        if (this.selectedBreed.slimeEffects().isEmpty()) {
            guiGraphics.drawString(this.font, Component.translatable("gui.splendid_slimes.slimepedia.no_effects"), this.leftPos + 96, topPosOffset, 0x5088a4, false);
        } else {
            for (SlimeEffect slimeEffect : this.selectedBreed.slimeEffects()) {
                Component effectTypeComponent = Component.empty();
                if (slimeEffect.getEffectType() == SlimeEffectType.POSITIVE) {
                    effectTypeComponent = Component.translatable("gui.splendid_slimes.slimepedia.positive_effect").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_GREEN);
                } else if (slimeEffect.getEffectType() == SlimeEffectType.NEGATIVE) {

                    effectTypeComponent = Component.translatable("gui.splendid_slimes.slimepedia.negative_effect").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.RED);
                } else if (slimeEffect.getEffectType() == SlimeEffectType.ATTACK) {
                    effectTypeComponent = Component.translatable("gui.splendid_slimes.slimepedia.attack_effect").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.RED);
                }
                guiGraphics.drawString(this.font, effectTypeComponent, this.leftPos + 96, topPosOffset, 0x5088a4, false);
                topPosOffset += 14;
                for (MobEffectInstance mobEffectInstance : slimeEffect.getEmitEffects()) {
                    MobEffect effect = mobEffectInstance.getEffect().value();
                    ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
                    ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(effectId.getNamespace(), "textures/mob_effect/" + effectId.getPath() + ".png");

                    guiGraphics.blit(textureLocation, this.leftPos + 96, topPosOffset, 0, 0, 0, 18, 18, 18, 18);
                    guiGraphics.drawString(this.font, Component.literal(effect.getDisplayName().getString() + " " + Component.translatable("potion.potency." + mobEffectInstance.getAmplifier()).getString()), this.leftPos + 96 + 22, topPosOffset, 0x5088a4, false);
                    guiGraphics.drawString(this.font, MobEffectUtil.formatDuration(mobEffectInstance, 1.0F, 20.0F), this.leftPos + 96 + 22, topPosOffset + 10, 0x5088a4, false);


                    topPosOffset += 22;
                }
            }
        }
        guiGraphics.disableScissor();

        int maxScroll = getMaxScrollOffset();
        if (maxScroll > 0) {
            renderTraitsScroller(guiGraphics, startX + TRAITS_TAB_WIDTH + 2, startY, TRAITS_TAB_HEIGHT, maxScroll);
        }
    }
    private int calculateTraitsContentHeight() {
        if (this.selectedBreed == null) return 0;
        int height = 0;
        height += 14;
        if (this.selectedBreed.traits().isEmpty()) {
            height += 14;
        } else {
            for (SlimeTrait trait : this.selectedBreed.traits()) {
                height += 12;
                int lines = (int) Math.ceil((double) this.font.width(Component.translatable("trait.splendid_slimes." + trait.getSerializedName() + ".info")) / TRAITS_TAB_WIDTH);
                height += (11 * lines) + 14;
            }
        }
        height += 14;
        if (this.selectedBreed.slimeEffects().isEmpty()) {
            height += 14;
        } else {
            for (SlimeEffect slimeEffect : this.selectedBreed.slimeEffects()) {
                height += 14;
                for (MobEffectInstance yeet : slimeEffect.getEmitEffects()) {
                    height += 22;
                }
            }
        }
        return height;
    }

    private int getMaxScrollOffset() {
        return Math.max(0, calculateTraitsContentHeight() - TRAITS_TAB_HEIGHT);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (this.selectedBreed != null && this.selectedState == SlimepediaState.TRAITS) {
            int startX = this.leftPos + TRAITS_TAB_X;
            int startY = this.topPos + TRAITS_TAB_Y;
            if (pMouseX >= startX && pMouseX <= startX + TRAITS_TAB_WIDTH + 15 && pMouseY >= startY && pMouseY <= startY + TRAITS_TAB_HEIGHT) {
                int maxScroll = getMaxScrollOffset();
                if (maxScroll > 0) {
                    this.scrollOff = Mth.clamp((int) (this.scrollOff - pScrollY * 16), 0, maxScroll);
                    return true;
                }
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.selectedBreed != null && this.selectedState == SlimepediaState.TRAITS) {
            int startX = this.leftPos + TRAITS_TAB_X + TRAITS_TAB_WIDTH + 2;
            int startY = this.topPos + TRAITS_TAB_Y;
            int maxScroll = getMaxScrollOffset();
            if (maxScroll > 0 && pMouseX >= startX && pMouseX <= startX + 6 && pMouseY >= startY && pMouseY <= startY + TRAITS_TAB_HEIGHT) {
                this.isDragging = true;
                return true;
            }
        }
        this.isDragging = false;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.isDragging && this.selectedBreed != null && this.selectedState == SlimepediaState.TRAITS) {
            int startY = this.topPos + TRAITS_TAB_Y;
            int maxScroll = getMaxScrollOffset();
            if (maxScroll > 0) {
                int scrollerHeight = Math.max(10, TRAITS_TAB_HEIGHT * TRAITS_TAB_HEIGHT / calculateTraitsContentHeight());
                int trackHeight = TRAITS_TAB_HEIGHT - scrollerHeight;
                this.scrollOff = Mth.clamp((int) ((pMouseY - startY - (scrollerHeight / 2.0)) / (double) (trackHeight) * maxScroll), 0, maxScroll);
                return true;
            }
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    
    private void renderTraitsScroller(GuiGraphics guiGraphics, int x, int y, int height, int maxScroll) {
        int trackHeight = height - SCROLLER_HEIGHT - 4;
        int scrollerY = y + (int) ((double) this.scrollOff / maxScroll * trackHeight);
        guiGraphics.blit(GUI_LOCATION, x, scrollerY, 0, 224, 0.0F, 2, SCROLLER_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    class SlimeBreedButton extends ImageButton {
        final int index;
        final ItemStack icon;

        public SlimeBreedButton(int pX, int pY, int pIndex, int pWidth, int pHeight, ItemStack icon, WidgetSprites pWidgetSprites, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
            this.index = pIndex;
            this.icon = icon;
            DynamicHolder<SlimeBreed> breed = PlortItem.getStoredPlort(icon);
            if (breed.isBound()) {
                this.setTooltip(Tooltip.create(breed.get().name().withColor(breed.get().color().getValue())));
            } else {
                this.setTooltip(Tooltip.create(Component.literal("Obtain Slime to unlock!")));
            }
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

        public SlimeInfoButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
            this.setTooltip(Tooltip.create(Component.translatable("gui.splendid_slimes.slimepedia.info_button")));
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

        public SlimeDietButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
            this.setTooltip(Tooltip.create(Component.translatable("gui.splendid_slimes.slimepedia.diet_button")));
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

        public SlimeTraitButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
            this.setTooltip(Tooltip.create(Component.translatable("gui.splendid_slimes.slimepedia.trait_button")));
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

    @OnlyIn(Dist.CLIENT)
    class NextButton extends ImageButton {

        public NextButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
        }


        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 16;
            int v = 144;

            if (!this.active) {
                v += 16;

            } else if (this.isHoveredOrFocused()) {
                v += 16;
            }

            guiGraphics.blit(GUI_LOCATION, this.getX(), this.getY(), u, v, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class PrevButton extends ImageButton {

        public PrevButton(int pX, int pY, int pWidth, int pHeight, WidgetSprites pWidgetSprites, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, pWidgetSprites, pOnPress);
        }


        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 0;
            int v = 144;

            if (!this.active) {
                v += 16;

            } else if (this.isHoveredOrFocused()) {
                v += 16;
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