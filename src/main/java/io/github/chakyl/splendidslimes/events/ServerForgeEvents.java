//package io.github.chakyl.splendidslimes.events;
//
//import io.github.chakyl.splendidslimes.SplendidSlimes;
//import io.github.chakyl.splendidslimes.tag.SplendidSlimesItemTags;
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
//
//import static io.github.chakyl.splendidslimes.util.SlimeVacUtils.dropLargo;
// TODO: Slime Vac
//public class ServerForgeEvents {
//    @EventBusSubscriber(modid = SplendidSlimes.MODID)
//    public static class ForgeEvents {
//        @SubscribeEvent
//        public static void onUse(PlayerInteractEvent event) {
//            LivingEntity player = event.getEntity();
//            ItemStack eventItem = event.getItemStack();
//            if (player instanceof Player && eventItem.is(SplendidSlimesItemTags.SLIME_VAC_FIREABLE)) {
//                if (!event.isCancelable() || eventItem.getItem() instanceof SlimeVac) return;
//                if (player.getOffhandItem().getItem() instanceof SlimeVac || player.getMainHandItem().getItem() instanceof SlimeVac) {
//                    event.setCanceled(true);
//                }
//            }
//        }
//
////        @SubscribeEvent
////        public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
////            if (!(event.getEntity() instanceof Player player)) return;
////            if (player.level().isClientSide) return;
////            if (event.getSlot() == EquipmentSlot.MAINHAND) {
////                ItemStack oldStack = event.getFrom();
////                ItemStack newStack = event.getTo();
////
////                if (oldStack.getItem() instanceof SlimeVac) {
////                    if (newStack.getItem() != oldStack.getItem()) {
////                        dropLargo(player.level(), player, oldStack);
////                    }
////                }
////            }
////        }
//    }
//}
