package io.github.chakyl.splendidslimes.item;

import com.mojang.blaze3d.shaders.Effect;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.util.SlimeComfortUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SlimeInspector extends Item {

    public SlimeInspector(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        list.add(Component.translatable("info.splendid_slimes.slime_inspector").withStyle(ChatFormatting.GRAY));
    }

    public void runInspection(Player player, SplendidSlime splendidSlime) {
        if (player.getCooldowns().isOnCooldown(ModElements.Items.SLIME_INSPECTOR.get())) return;
        boolean allGood = true;
        List<Component> messagesToSend = new ArrayList<>();
        if (SlimeComfortUtils.slimeIsSuffocated(splendidSlime)) {
            messagesToSend.add(Component.translatable("info.splendid_slimes.slime_inspector.suffocating").withStyle(ChatFormatting.DARK_RED));
            allGood = false;
        }
        List<SplendidSlime> nearbyFriends = SlimeComfortUtils.getNearbyFriends(splendidSlime);
        if (nearbyFriends.size() > 8) {
            messagesToSend.add(Component.translatable("info.splendid_slimes.slime_inspector.crowded", nearbyFriends.size()).withStyle(ChatFormatting.GOLD));
            for (SplendidSlime friend : nearbyFriends) {
                friend.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0, false, false));
            }
        }
        if (SlimeComfortUtils.diverseTraitCheck(splendidSlime)) {
            messagesToSend.add(Component.translatable("info.splendid_slimes.slime_inspector.diverse_failure").withStyle(ChatFormatting.LIGHT_PURPLE));
            allGood = false;
        }
        if (SlimeComfortUtils.aquaticTraitCheck(splendidSlime)) {
            messagesToSend.add(Component.translatable("info.splendid_slimes.slime_inspector.aquatic_failure").withStyle(ChatFormatting.AQUA));
            allGood = false;
        }
        if (allGood) {
            messagesToSend.add(Component.translatable("info.splendid_slimes.slime_inspector.all_good").withStyle(ChatFormatting.GREEN));
        } else {
            messagesToSend.add(Component.translatable("info.splendid_slimes.slime_inspector.not_happy").withStyle(ChatFormatting.RED));
        }
        if (player.level().isClientSide) {
            for (Component message : messagesToSend) {
                player.sendSystemMessage(message);
            }
        }
        splendidSlime.playSound(SoundEvents.ALLAY_ITEM_GIVEN, 1.0F, 0.2F);
        player.getCooldowns().addCooldown(ModElements.Items.SLIME_INSPECTOR.get(), 120);
    }
}
