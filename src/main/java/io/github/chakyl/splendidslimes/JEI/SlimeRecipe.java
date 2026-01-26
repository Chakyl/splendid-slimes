package io.github.chakyl.splendidslimes.JEI;

import io.github.chakyl.splendidslimes.data.SlimeBreed;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;

public class SlimeRecipe {
    final ItemStack slime;
    final ItemStack heart;
    public SlimeRecipe(SlimeBreed breed) {
        this.slime = breed.getSlimeItem().copy();
        this.heart = breed.getHeart().copy();
    }

}