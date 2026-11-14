package io.github.chakyl.splendidslimes.events;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.client.Keybindings;
import io.github.chakyl.splendidslimes.client.renderer.SlimeVacRenderer;
import io.github.chakyl.splendidslimes.network.PacketHandler;
import io.github.chakyl.splendidslimes.network.ServerBoundSlimeVacModePacket;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import static io.github.chakyl.splendidslimes.util.SlimeVacUtils.getPerspective;

@EventBusSubscriber(value = Dist.CLIENT, modid = SplendidSlimes.MODID)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (Keybindings.INSTANCE.slimeVacModeKey.consumeClick()) {
            PacketHandler.sendToServer(new ServerBoundSlimeVacModePacket());
        }
    }


    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        MultiBufferSource buffer = event.getMultiBufferSource();
        PoseStack matrix = event.getPoseStack();
        int light = event.getPackedLight();
        float partialTicks = event.getPartialTick();

        if(SlimeVacRenderer.drawFirstPerson(player, buffer, matrix, light, partialTicks) && getPerspective() == 0) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event)  {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            SlimeVacRenderer.drawThirdPerson(event.getPartialTick().getRealtimeDeltaTicks(), event.getPoseStack().last().pose());
    }

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        if (player.getMainHandItem().getItem() == ModElements.Items.SLIME_VAC.value()) {
            event.getRenderer().getModel().rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
    }
}
