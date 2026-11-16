package io.github.chakyl.splendidslimes.entity.slimethetics;

import com.mojang.serialization.Codec;
import io.github.chakyl.splendidslimes.registry.ModElements;

import java.util.ArrayList;
import java.util.List;

public class SlimeDecorators extends ArrayList<SlimeDecorator> {
    public static final Codec<SlimeDecorators> CODEC = SlimeDecorator.CODEC.listOf().xmap(
            SlimeDecorators::new,
            list -> list
    );

    public SlimeDecorators() {
    }

    public SlimeDecorators(List<SlimeDecorator> list) {
        super(list);
    }
}