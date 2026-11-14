//package io.github.chakyl.splendidslimes.datagen;
//
//import io.github.chakyl.splendidslimes.SplendidSlimes;
//import net.minecraft.data.recipes.FinishedRecipe;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Consumer;
//
//public class PlortPressRecipes {
//    public static void register(Consumer<FinishedRecipe> consumer) {
//        List<String> breeds = Arrays.asList("all_seeing", "bitwise", "boomcat", "blazing", "bony", "ender", "gold", "luminous", "minty", "orby", "phantom", "prisma", "puddle", "rotting", "shulking", "slimy", "sweet", "webby", "weeping");
//        for (String breed : breeds) {
//            SplendidSlimes.LOGGER.info("generating pressing recipe for " + breed);
//            PlortPressRecipeBuilder.pressRecipe(breed).build(consumer);;
//        }
//    }
//}