package io.github.chakyl.splendidslimes.JEI;

import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.entity.slimetraits.SlimeTrait;
import net.minecraft.world.item.ItemStack;

public class TraitRecipe {
    final ItemStack slime;
    final SlimeTrait trait;

    public TraitRecipe(SlimeBreed breed, SlimeTrait trait) {
        this.slime = breed.getSlimeItem().copy();
        this.trait = trait;
    }
}