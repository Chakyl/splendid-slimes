//package io.github.chakyl.splendidslimes.datagen;
//
//import io.github.chakyl.splendidslimes.SplendidSlimes;
//import io.github.chakyl.splendidslimes.registry.ModElements;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.data.PackOutput;
//import net.minecraft.tags.BlockTags;
//import net.minecraftforge.common.data.BlockTagsProvider;
//import net.minecraftforge.common.data.ExistingFileHelper;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.concurrent.CompletableFuture;
//
//public class ModBlockTagGenerator extends BlockTagsProvider {
//    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
//        super(output, lookupProvider, SplendidSlimes.MODID, existingFileHelper);
//    }
//
//    @Override
//    protected void addTags(HolderLookup.Provider pProvider) {
//
//        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
//                .add(ModElements.Blocks.CORRAL_BLOCK.get(),
//                        ModElements.Blocks.CORRAL_PANE.get(),
//                        ModElements.Blocks.SLIME_INCUBATOR.get(),
//                        ModElements.Blocks.PLORT_RIPPIT.get(),
//                        ModElements.Blocks.SLIME_FEEDER.get(),
//                        ModElements.Blocks.PLORT_PRESS.get());
//
//        this.tag(BlockTags.NEEDS_STONE_TOOL)
//                .add(ModElements.Blocks.SLIME_INCUBATOR.get(),
//                        ModElements.Blocks.PLORT_RIPPIT.get(),
//                        ModElements.Blocks.PLORT_PRESS.get());
//
//
//    }
//}