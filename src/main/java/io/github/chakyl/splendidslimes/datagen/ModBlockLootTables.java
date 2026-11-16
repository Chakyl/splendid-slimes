//package io.github.chakyl.splendidslimes.datagen;
//
//
//import io.github.chakyl.splendidslimes.registry.ModElements;
//import net.minecraft.data.loot.BlockLootSubProvider;
//import net.minecraft.world.flag.FeatureFlags;
//import net.minecraft.world.level.block.Block;
//
//import java.util.ArrayList;
//import java.util.Set;
//
//public class ModBlockLootTables extends BlockLootSubProvider {
//    public ModBlockLootTables() {
//        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
//    }
//
//    @Override
//    protected void generate() {
//        this.dropSelf(ModElements.Blocks.CORRAL_BLOCK.get());
//        this.dropSelf(ModElements.Blocks.CORRAL_PANE.get());
//        this.dropSelf(ModElements.Blocks.PLORT_PRESS.get());
//        this.dropSelf(ModElements.Blocks.PLORT_RIPPIT.get());
//        this.dropSelf(ModElements.Blocks.SLIME_FEEDER.get());
//        this.dropSelf(ModElements.Blocks.SLIME_INCUBATOR.get());
//
//    }
//    @Override
//    protected Iterable<Block> getKnownBlocks() {
//        ArrayList<Block> blocks = new ArrayList<>();
//        blocks.add(ModElements.Blocks.CORRAL_BLOCK.get());
//        blocks.add(ModElements.Blocks.CORRAL_PANE.get());
//        blocks.add(ModElements.Blocks.PLORT_PRESS.get());
//        blocks.add(ModElements.Blocks.PLORT_RIPPIT.get());
//        blocks.add(ModElements.Blocks.SLIME_FEEDER.get());
//        blocks.add(ModElements.Blocks.SLIME_INCUBATOR.get());
//        return blocks;
//    }
//}