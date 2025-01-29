package net.happyspeed.raid_on.item.custom;

import com.google.common.collect.Sets;
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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!user.getWorld().isClient()) {
            user.sendMessage(Text.translatable("message.raid_on.locating_raiders").formatted(Formatting.GRAY).formatted(Formatting.ITALIC),true);
            if (user.getWorld() instanceof ServerWorld serverWorld) {
                Raid raid = serverWorld.getRaidAt(user.getBlockPos());
                if (raid != null) {
                    for (RaiderEntity raider : raid.getAllRaiders()) {
                        raider.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 1), user);
                    }
                }
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
