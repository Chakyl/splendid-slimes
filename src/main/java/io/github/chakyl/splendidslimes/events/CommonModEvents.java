package io.github.chakyl.splendidslimes.events;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = SplendidSlimes.MODID)
public class CommonModEvents {
    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModElements.Entities.SPLENDID_SLIME.value(), SlimeEntityBase.createAttributes().build());
        event.put(ModElements.Entities.TARR.value(), SlimeEntityBase.createAttributes().build());
    }
// TODO: Slime Vac
//    @SubscribeEvent
//    public static void commonSetup(FMLCommonSetupEvent event) {
//        event.enqueueWork(() ->{
//            PacketHandler.register();
//        });
//    }

}
