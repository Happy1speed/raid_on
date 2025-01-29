package net.happyspeed.raid_on.item.custom;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.World;

public class RaidOMeter extends Item {
    public RaidOMeter(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!user.getWorld().isClient()) {
            if (user.getWorld() instanceof ServerWorld serverWorld) {
                Raid raid = serverWorld.getRaidAt(user.getBlockPos());

                if (raid != null) {
                    user.sendMessage(Text.translatable("message.raid_on.wavescount").append(String.valueOf(raid.waveCount)).formatted(Formatting.DARK_PURPLE).formatted(Formatting.ITALIC));
                    user.sendMessage(Text.translatable("message.raid_on.possible_extra_wave").formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.ITALIC));
                    user.sendMessage(Text.translatable("message.raid_on.currentraidercount").append(String.valueOf(raid.getRaiderCount())).formatted(Formatting.DARK_GREEN).formatted(Formatting.ITALIC));
                    for (int i = 0; i < user.getInventory().size(); i++) {
                        if (user.getInventory().getStack(i).getItem() instanceof RaidOMeter) {
                            user.getItemCooldownManager().set(user.getInventory().getStack(i).getItem(), 100);
                        }
                    }
                }
                else {
                    user.sendMessage(Text.translatable("message.raid_on.notinraidarea").formatted(Formatting.YELLOW).formatted(Formatting.ITALIC));
                }
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
