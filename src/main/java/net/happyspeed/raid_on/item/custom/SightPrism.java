package net.happyspeed.raid_on.item.custom;

import com.google.common.collect.Sets;
import net.happyspeed.raid_on.mixin.PlayerEntityMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SightPrism extends Item {
    public SightPrism(Settings settings) {
        super(settings);
    }


    public void activeAbility(World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            if (!player.getWorld().isClient()) {
                player.sendMessage(Text.translatable("message.raid_on.locating_raiders").formatted(Formatting.GRAY).formatted(Formatting.ITALIC), true);
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    Raid raid = serverWorld.getRaidAt(player.getBlockPos());
                    if (raid != null) {
                        for (RaiderEntity raider : raid.getAllRaiders()) {
                            raider.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 5, 1, false, false), player);
                        }
                    }
                }
            }
        }
    }
}
