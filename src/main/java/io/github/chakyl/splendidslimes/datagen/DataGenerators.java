//package io.github.chakyl.splendidslimes.datagen;
//
//import io.github.chakyl.splendidslimes.SplendidSlimes;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.data.PackOutput;
//import net.minecraftforge.common.data.ExistingFileHelper;
//import net.minecraftforge.data.event.GatherDataEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import java.util.concurrent.CompletableFuture;
//
//@Mod.EventBusSubscriber(modid = SplendidSlimes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class DataGenerators {
//    @SubscribeEvent
//    public static void gatherData(GatherDataEvent event) {
//        DataGenerator generator = event.getGenerator();
//        PackOutput packOutput = generator.getPackOutput();
//        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
//        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
//
//        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput));
//        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
////        generator.addProvider(event.includeClient(), new ModLangProvider(packOutput, SplendidSlimes.MODID, "en_us"));
//
//        ModBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(),
//                new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
//    }
//}