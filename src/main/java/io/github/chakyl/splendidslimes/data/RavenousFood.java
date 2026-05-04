package io.github.chakyl.splendidslimes.data;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class RavenousFood {
    private final ItemStack food;
    private final int hungerAmount;

    public RavenousFood(ItemStack food, int hungerAmount) {
        this.food = food;
        this.hungerAmount = hungerAmount;
    }
    public ItemStack getFood() {
        return this.food;
    }

    public int getHungerAmount() {
        return this.hungerAmount;
    }
}
