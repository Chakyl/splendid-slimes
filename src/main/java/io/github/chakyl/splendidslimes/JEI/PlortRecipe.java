package io.github.chakyl.splendidslimes.JEI;

import io.github.chakyl.splendidslimes.data.SlimeBreed;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;

public class PlortRecipe {
    final ItemStack slime;
    final ItemStack output;
     final ArrayList<Ingredient> inputs = new ArrayList<>();

    public PlortRecipe(SlimeBreed breed) {
        this.slime = breed.getSlimeItem().copy();
        for (Ingredient food : breed.foods()) {
            this.inputs.add(food);
        }
        this.output = breed.getPlort().copy();
    }

}