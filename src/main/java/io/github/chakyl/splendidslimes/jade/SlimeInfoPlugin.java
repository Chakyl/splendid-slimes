package io.github.chakyl.splendidslimes.jade;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.block.SlimeIncubatorBlock;
import io.github.chakyl.splendidslimes.block.SlimeSpawnerBlock;
import io.github.chakyl.splendidslimes.blockentity.SlimeIncubatorBlockEntity;
import io.github.chakyl.splendidslimes.blockentity.SlimeSpawnerBlockEntity;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SlimeInfoPlugin implements IWailaPlugin {
    public static final ResourceLocation UID = new ResourceLocation(SplendidSlimes.MODID, "splendid_slime");
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(SlimeInfoComponentProvider.INSTANCE, SplendidSlime.class);
        registration.registerBlockDataProvider(SlimeSpawnerInfoComponentProvider.INSTANCE, SlimeSpawnerBlockEntity.class);;
        registration.registerBlockDataProvider(SlimeIncubatorInfoComponentProvider.INSTANCE, SlimeIncubatorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(SlimeInfoComponentProvider.INSTANCE, SplendidSlime.class);
        registration.registerBlockComponent(SlimeSpawnerInfoComponentProvider.INSTANCE, SlimeSpawnerBlock.class);
        registration.registerBlockComponent(SlimeIncubatorInfoComponentProvider.INSTANCE, SlimeIncubatorBlock.class);
    }
}