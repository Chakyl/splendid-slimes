package io.github.chakyl.splendidslimes.item;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DecoratorItem extends Item {
    public DecoratorItem(Properties pProperties) {
        super(pProperties);
    }

    public static void setStoredDecorator(ItemStack stack, SlimeBreed slimeBreed) {
        setStoredDecorator(stack, SlimeBreedRegistry.INSTANCE.holder(slimeBreed));
    }

    public static DynamicHolder<SlimeBreed> setStoredDecorator(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }

    public static DynamicHolder<SlimeBreed> getStoredDecorator(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }

    public static void setStoredDecorator(ItemStack stack, ResourceLocation location) {
        stack.getOrDefault(ModElements.Components.SLIME_BREED, location.toString());
    }

    public static void setStoredDecorator(ItemStack stack, DynamicHolder<SlimeBreed> slimeBreed) {
        stack.set(ModElements.Components.SLIME_BREED, slimeBreed);
    }
}