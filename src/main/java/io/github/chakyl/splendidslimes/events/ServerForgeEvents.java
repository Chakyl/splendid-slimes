package io.github.chakyl.splendidslimes.events;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.item.SlimeVac;
import io.github.chakyl.splendidslimes.tag.SplendidSlimesItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ServerForgeEvents {
    @Mod.EventBusSubscriber(modid = SplendidSlimes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onUse(PlayerInteractEvent event) {
            LivingEntity player = event.getEntity();
            ItemStack eventItem = event.getItemStack();
            if (player instanceof Player && eventItem.is(SplendidSlimesItemTags.SLIME_VAC_FIREABLE)) {
                if (!event.isCancelable() || eventItem.getItem() instanceof SlimeVac) return;
                if (player.getOffhandItem().getItem() instanceof SlimeVac || player.getMainHandItem().getItem() instanceof SlimeVac) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
