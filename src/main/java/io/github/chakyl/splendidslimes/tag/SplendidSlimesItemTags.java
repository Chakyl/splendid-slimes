package io.github.chakyl.splendidslimes.tag;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class SplendidSlimesItemTags {
    public static final TagKey<Item> SLIME_VAC_FIREABLE = tag("slime_vac_fireable");
    public static TagKey<Item> tag(String name) {
        return ItemTags.create(loc( name));
    }
}