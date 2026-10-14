package io.github.chakyl.splendidslimes.item;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public class PlortItem extends Item implements ITabFiller {
    public PlortItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, BuildCreativeModeTabContentsEvent event) {
        SlimeBreedRegistry.INSTANCE.getKeys().stream().sorted().map(SlimeBreedRegistry.INSTANCE::holder).forEach(holder -> {
            ItemStack s = new ItemStack(this);
            setStoredPlort(s, holder);
            event.accept(s);
        });
    }

    @Override
    public Component getName(ItemStack pStack) {
        DynamicHolder<SlimeBreed> slime = setStoredPlort(pStack);
        MutableComponent name;
        if (!slime.isBound()) {
            name = Component.translatable("item.splendid_slimes.default_plort");
        } else {
            name = slime.get().name().copy();
            name.withColor(slime.get().color().getValue());
        }
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