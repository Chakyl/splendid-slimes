package io.github.chakyl.splendidslimes.util;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlimeComfortUtils {
    private static final int NEARBY_CHECK_RADIUS = 5;
    private static final double NEARBY_SUFFOCATED_RADIUS = 0.2;

    public static boolean slimeIsSimilar(SplendidSlime slime1, SplendidSlime slime2) {
        if (Objects.equals(slime1.getSlimeBreed(), slime2.getSlimeBreed())) return true;
        if (!slime1.getSlimeSecondaryBreed().isEmpty()) {
            if (Objects.equals(slime1.getSlimeSecondaryBreed(), slime2.getSlimeBreed())) return true;
            if (Objects.equals(slime1.getSlimeSecondaryBreed(), slime2.getSlimeSecondaryBreed())) return true;
        }
        if (!slime2.getSlimeSecondaryBreed().isEmpty()) {
            return Objects.equals(slime2.getSlimeSecondaryBreed(), slime1.getSlimeBreed());
        }
        return false;
    }

    public static List<SplendidSlime> getNearbyFriends(SplendidSlime splendidSlime) {
        return splendidSlime.level().getEntitiesOfClass(SplendidSlime.class, splendidSlime.getBoundingBox().inflate(NEARBY_CHECK_RADIUS), e -> slimeIsSimilar(e, splendidSlime));
    }

    public static List<SplendidSlime> getNearbyDifferent(SplendidSlime splendidSlime) {
        return splendidSlime.level().getEntitiesOfClass(SplendidSlime.class, splendidSlime.getBoundingBox().inflate(NEARBY_CHECK_RADIUS), e -> !slimeIsSimilar(e, splendidSlime));
    }

    public static boolean slimeIsSuffocated(SplendidSlime splendidSlime) {
        return splendidSlime.level().getEntitiesOfClass(SplendidSlime.class, splendidSlime.getBoundingBox().inflate(NEARBY_SUFFOCATED_RADIUS)).size() > 3;
    }

    public static boolean photosynthesizingTraitCheck(SplendidSlime splendidSlime) {
        return splendidSlime.hasTrait("photosynthesizing") && !splendidSlime.level().canSeeSkyFromBelowWater(splendidSlime.getOnPos());
    }

    public static boolean aquaticTraitCheck(SplendidSlime splendidSlime) {
        return splendidSlime.hasTrait("aquatic") && !splendidSlime.isInWater();
    }

    public static boolean diverseTraitCheck(SplendidSlime splendidSlime) {
        if (splendidSlime.hasTrait("diverse")) {
            List<String> breeds = new ArrayList<>();
            List<SplendidSlime> nearbyDifferent = SlimeComfortUtils.getNearbyDifferent(splendidSlime);
            for (SplendidSlime slime : nearbyDifferent) {
                if (!breeds.contains(slime.getSlimeBreed())) {
                    breeds.add(slime.getSlimeBreed());
                }
                if (!breeds.contains(slime.getSlimeSecondaryBreed())) {
                    breeds.add(slime.getSlimeSecondaryBreed());
                }
                if (breeds.size() >= 3) break;
            }
            return breeds.size() < 3;
        }
        return false;
    }
}
