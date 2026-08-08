package io.github.chakyl.splendidslimes.util;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SlimeData {
    public static DynamicHolder<SlimeBreed> getSlimeData(ItemStack stack, String nbtAccessor) {
        CompoundTag tag = stack.getTagElement(nbtAccessor);
        if (stack.isEmpty() || tag == null || !tag.contains("id")) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        return SlimeBreedRegistry.INSTANCE.holder(ResourceLocation.parse(tag.getString("id")));
    }

    public static DynamicHolder<SlimeBreed> getSlimeFromEgg(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("EntityTag");
        if (stack.isEmpty() || tag == null || !tag.contains("Breed")) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        return SlimeBreedRegistry.INSTANCE.holder(ResourceLocation.parse(tag.getString("Breed")));
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
