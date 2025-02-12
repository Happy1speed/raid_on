package net.happyspeed.raid_on.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class OmenUp extends Item {

    public OmenUp(Settings settings) {
        super(settings);
    }

    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }

        if (!world.isClient && playerEntity != null) {
            if (user.hasStatusEffect(StatusEffects.BAD_OMEN)) {
                int omenlevel = Objects.requireNonNull(playerEntity.getStatusEffect(StatusEffects.BAD_OMEN)).getAmplifier();
                if (omenlevel + 1 <= 50) {
                    playerEntity.setStatusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 500000, omenlevel + 1), playerEntity);
                    playerEntity.getWorld().playSound(null, playerEntity.getBlockPos(), SoundEvents.ENTITY_PILLAGER_CELEBRATE, SoundCategory.PLAYERS, 1.0f, 0.7f);
                    playerEntity.sendMessage(Text.translatable("message.raid_on.bad_omen_worse").formatted(Formatting.DARK_RED).formatted(Formatting.BOLD), true);
                }
            }
        }

        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 16;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
