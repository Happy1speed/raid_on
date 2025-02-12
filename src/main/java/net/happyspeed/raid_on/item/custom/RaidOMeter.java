package net.happyspeed.raid_on.item.custom;

import com.google.common.collect.Lists;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RaidOMeter extends Item {
    public RaidOMeter(Settings settings) {
        super(settings);
    }

    public void doThing(ItemStack stack, World world, LivingEntity living) {
        if (living instanceof PlayerEntity user) {
            if (!user.getWorld().isClient()) {
                if (user.getWorld() instanceof ServerWorld serverWorld) {
                    Raid raid = serverWorld.getRaidAt(user.getBlockPos());
                    if (raid != null) {
                        user.sendMessage(Text.translatable("message.raid_on.wavescount").formatted(Formatting.DARK_GREEN).formatted(Formatting.UNDERLINE).append(String.valueOf(raid.waveCount + (raid.hasExtraWave() ? 1 : 0))).append(Text.literal(" | ").formatted(Formatting.GRAY)).append(Text.translatable("message.raid_on.currentraidercount").append(String.valueOf(raid.getRaiderCount())).formatted(Formatting.DARK_AQUA).formatted(Formatting.UNDERLINE)), true);
                    }
                    else {
                        user.sendMessage(Text.translatable("message.raid_on.notinraidarea").formatted(Formatting.YELLOW).formatted(Formatting.ITALIC), true);
                    }
                }
            }
        }
    }

}
