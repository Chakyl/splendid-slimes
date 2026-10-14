package io.github.chakyl.splendidslimes.item;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.List;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public class SlimeHeartItem extends Item implements ITabFiller {
    public SlimeHeartItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("info.splendid_slimes.slime_heart").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, BuildCreativeModeTabContentsEvent event) {
        SlimeBreedRegistry.INSTANCE.getKeys().stream().sorted().map(SlimeBreedRegistry.INSTANCE::holder).forEach(holder -> {
            ItemStack s = new ItemStack(this);
            setStoredPlort(s, holder);
            event.accept(s);
        });
    }

    public Component getName(ItemStack pStack) {
        DynamicHolder<SlimeBreed> slime = setStoredPlort(pStack);
        Component name;
        if (!slime.isBound()) {
            name = Component.translatable("item.splendid_slimes.default_heart");
        } else name = slime.get().name().copy();
        return Component.translatable(this.getDescriptionId(pStack), name);
    }

    public static DynamicHolder<SlimeBreed> setStoredPlort(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }

    public static DynamicHolder<SlimeBreed> getStoredPlort(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }
    public static void setStoredPlort(ItemStack stack, SlimeBreed slimeBreed) {
        setStoredPlort(stack, SlimeBreedRegistry.INSTANCE.holder(slimeBreed));
    }

    public static void setStoredPlort(ItemStack stack, DynamicHolder<SlimeBreed> slimeBreed) {
        stack.set(ModElements.Components.SLIME_BREED, slimeBreed);
    }

}


