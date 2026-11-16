//package io.github.chakyl.splendidslimes.datagen;
//
//import io.github.chakyl.splendidslimes.SplendidSlimes;
//import net.minecraft.data.PackOutput;
//import net.minecraftforge.common.data.LanguageProvider;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class ModLangProvider extends  LanguageProvider {
//    public ModLangProvider(PackOutput output, String modid, String locale) {
//        super(output, modid, locale);
//    }
//
//    @Override
//    protected void addTranslations() {
//        this.add("itemGroup.splendid_slimes", "Splendid Slimes");
//        this.add("block.splendid_slimes.slime_incubator", "Slime Incubator");
//        this.add("block.splendid_slimes.plort_rippit", "Plort Rippit");
//        this.add("block.splendid_slimes.plort_press", "Plort Press");
//        this.add("block.splendid_slimes.corral_block", "Corral Block");
//        this.add("block.splendid_slimes.corral_pane", "Corral Pane");
//
//        this.add("item.splendid_slimes.slime_vac", "Slime Vac");
//        this.add("item.splendid_slimes.plort", "%s Plort");
//        this.add("item.splendid_slimes.default_plort", "Any");
//        this.add("item.splendid_slimes.default_heart", "Any");
//        this.add("item.splendid_slimes.slime_heart", "%s Slime Heart");
//        this.add("item.splendid_slimes.spawn_egg_splendid_slime", "%s Slime Spawn Egg");
//        this.add("item.splendid_slimes.spawn_egg_tarr", "Tarr Spawn Egg");
//
//        this.add("info.splendid_slimes.slime_heart", "Place in Slime Incubator to grow!");
//        this.add("entity.splendid_slimes.splendid_slime", "%s Slime");
//        this.add("entity.splendid_slimes.tarr", "Tarr");
//        this.add("entity.splendid_slimes.largo_splendid_slime", "%s %s Largo");
//        this.add("diet.splendid_slimes.default_diet", "Any");
//        this.add("entity.splendid_slimes.diet", "Diet: %s");
//        this.add("entity.splendid_slimes.largo_diet", "Diet: %s, %s");
//        this.add("entity.splendid_slimes.happy", "Happy");
//        this.add("entity.splendid_slimes.furious", "Furious");
//        this.add("entity.splendid_slimes.sad", "Sad");
//        this.add("entity.splendid_slimes.neutral", "Neutral");
//        this.add("info.splendid_slimes.plort", "Dropped from fed slimes");
//        this.add("config.jade.plugin_splendid_slimes.splendid_slime", "jade slime")
//        ;
//        this.add("splendid_slimes.advancement.root.title", "Splendid Slimes");
//        this.add("splendid_slimes.advancement.root.description", "Ranching slimes for resources!");
//        this.add("splendid_slimes.advancement.obtain_press.title", "Im-Pressive Slimes");
//        this.add("splendid_slimes.advancement.obtain_press.description", "Craft a Plort Press to press Slime Hearts");
//        this.add("splendid_slimes.advancement.obtain_incubator.title", "The Birth of Slime");
//        this.add("splendid_slimes.advancement.obtain_incubator.description", "Craft a Slime Incubator to incubate Slime Hearts into Slimes");
//        this.add("splendid_slimes.advancement.obtain_plort.title", "Slime... Waste?");
//        this.add("splendid_slimes.advancement.obtain_plort.description", "Feed a Slime and collect a Plort");
//        this.add("splendid_slimes.advancement.obtain_rippit.title", "Let it Rip!");
//        this.add("splendid_slimes.advancement.obtain_rippit.description", "Craft a Plort Rippit to turn Plorts into usable resources");
//        this.add("splendid_slimes.advancement.obtain_corral.title", "Herding Slimes");
//        this.add("splendid_slimes.advancement.obtain_corral.description", "Use Corral Blocks to keep Slimes contained");
//
//        List<String> breeds = Arrays.asList("all_seeing", "bitwise", "boomcat", "blazing", "bony", "ender", "gold", "luminous", "minty", "orby", "phantom", "prisma", "puddle", "rotting", "shulking", "slimy", "sweet", "webby", "weeping");
//        for (String breed : breeds) {
//            this.add("slime." + SplendidSlimes.MODID + "." + breed,  breed.substring(0, 1).toUpperCase() + breed.substring(1).replace("_", "-"));
//        }
//        for (String breed : breeds) {
//            this.add("diet." + SplendidSlimes.MODID + "." + breed, "");
//        }
//    }
//}