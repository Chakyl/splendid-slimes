package io.github.chakyl.splendidslimes.item;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.entity.slimetraits.SlimeTrait;
import io.github.chakyl.splendidslimes.entity.slimetraits.SlimeTraits;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.List;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public class SlimeInventoryItem extends Item implements ITabFiller {

    public SlimeInventoryItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        DynamicHolder<SlimeBreed> slime = getSlimeData(stack);
        Component slimeDiet;
        SlimeTraits traits = new SlimeTraits();
        if (!slime.isBound()) {
            slimeDiet = Component.translatable("diet.splendid_slimes.default_diet");
        } else {
            slimeDiet = slime.get().diet();
            traits = slime.get().traits();
        }
        if (!traits.isEmpty()) {
            if (Screen.hasShiftDown()) {
                for (SlimeTrait trait : traits) {
                    tooltipComponents.add(Component.translatable("trait.splendid_slimes." + trait.getSerializedName() + ".name").withStyle(ChatFormatting.GRAY));
                    tooltipComponents.add(Component.translatable("trait.splendid_slimes." + trait.getSerializedName() + ".info").withStyle(ChatFormatting.DARK_GRAY));
                }
            } else {
                tooltipComponents.add(Component.translatable("entity.splendid_slimes.diet", slimeDiet).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("item.splendid_slimes.slime_item.tooltip_prompt"));
            }
        } else {
            tooltipComponents.add(Component.translatable("entity.splendid_slimes.diet", slimeDiet).withStyle(ChatFormatting.GRAY));
        }
    }
    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, BuildCreativeModeTabContentsEvent event) {
        SlimeBreedRegistry.INSTANCE.getKeys().stream().sorted().map(SlimeBreedRegistry.INSTANCE::holder).forEach(holder -> {
            ItemStack s = new ItemStack(this);
            setStoredSlime(s, holder);
            event.accept(s);
        });
    }
    @Override
    public Component getName(ItemStack pStack) {
        DynamicHolder<SlimeBreed> slime = getSlimeData(pStack);
        Component slimeName;
        if (!slime.isBound()) {
            slimeName = Component.translatable("item.splendid_slimes.default_slime_item");
        } else slimeName = slime.get().name();
        return Component.translatable(this.getDescriptionId(pStack), slimeName);
    }
    public static DynamicHolder<SlimeBreed> setStoredSlime(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }
    public static void setStoredSlime(ItemStack stack, SlimeBreed slimeBreed) {
        setStoredSlime(stack, SlimeBreedRegistry.INSTANCE.holder(slimeBreed));
    }

    public static void setStoredSlime(ItemStack stack, DynamicHolder<SlimeBreed> slimeBreed) {
        stack.set(ModElements.Components.SLIME_BREED, slimeBreed);
    }

    public static DynamicHolder<SlimeBreed> getStoredSlime(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }

    public static SplendidSlime getSlimeFromItem(Level level, ItemStack itemStack) {
        if (itemStack.has(ModElements.Components.SLIME_BREED)) {
            SplendidSlime slimeEntity = new SplendidSlime(ModElements.Entities.SPLENDID_SLIME.get(), level);
            DynamicHolder<SlimeBreed> slimeBreed = itemStack.get(ModElements.Components.SLIME_BREED);
            if (itemStack.has(ModElements.Components.SECONDARY_SLIME_BREED)) {
                DynamicHolder<SlimeBreed> secondaryBreed = itemStack.get(ModElements.Components.SLIME_BREED);
                if ((secondaryBreed == null || !secondaryBreed.isBound())) return null;
                slimeEntity.setSlimeSecondaryBreed(secondaryBreed.get().breed());
            }
            if (slimeBreed == null || !slimeBreed.isBound()) return null;
            slimeEntity.setSlimeBreed(slimeBreed.get().breed());
            if (itemStack.has(ModElements.Components.SLIME_DATA)) {
                slimeEntity.load(itemStack.get(ModElements.Components.SLIME_DATA).entity());
            }
            return slimeEntity;
        }
        return null;
    }
}