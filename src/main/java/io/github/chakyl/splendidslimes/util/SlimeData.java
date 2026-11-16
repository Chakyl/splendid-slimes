package io.github.chakyl.splendidslimes.util;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SlimeData {

    public static DynamicHolder<SlimeBreed> getSlimeData(ItemStack stack) {
        if (stack.isEmpty()) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        DynamicHolder<SlimeBreed> holder = stack.get(ModElements.Components.SLIME_BREED);
        return holder != null ? holder : SlimeBreedRegistry.INSTANCE.emptyHolder();
    }

    public static DynamicHolder<SlimeBreed> getSlimeFromEgg(ItemStack stack) {
        if (stack.isEmpty()) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        DynamicHolder<SlimeBreed> holder = stack.get(ModElements.Components.SLIME_BREED);
        return holder != null ? holder : SlimeBreedRegistry.INSTANCE.emptyHolder();
    }

    public static DynamicHolder<SlimeBreed> getSlimeData(String breed) {
        String resolvedBreed = breed.replace("\"", "");
        if (resolvedBreed.isEmpty()) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        return SlimeBreedRegistry.INSTANCE.holder(ResourceLocation.parse(resolvedBreed));
    }

    public static boolean plortIsFromLargoless(String plortTag) {
        if (plortTag.isEmpty()) return false;
        DynamicHolder<SlimeBreed> newSlime = SlimeData.getSlimeData(plortTag);
        return newSlime.isBound() && newSlime.get().traits().contains("largoless");
    }

    public static String parseCommand(String command) {
        if (command == null || command.length() < 3) return "";
        return command.substring(1, command.length() - 1).replace("\\\"", "\"");
    }
}