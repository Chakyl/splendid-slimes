package io.github.chakyl.splendidslimes.entity.slimetraits;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.chakyl.splendidslimes.entity.slimeffects.SlimeEffect;
import io.github.chakyl.splendidslimes.entity.slimethetics.SlimeDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlimeTraits extends ArrayList<SlimeTrait> {
    public static final Codec<SlimeTraits> CODEC = SlimeTrait.CODEC.listOf().xmap(
            SlimeTraits::new,
            list -> list
    );

    public SlimeTraits() {
    }

    public SlimeTraits(List<SlimeTrait> list) {
        super(list);
    }
}