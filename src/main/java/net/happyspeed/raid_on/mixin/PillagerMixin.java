package net.happyspeed.raid_on.mixin;

import com.google.common.collect.Lists;
import net.happyspeed.raid_on.config.ModConfigs;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.FireworkRocketRecipe;
import net.minecraft.recipe.FireworkStarFadeRecipe;
import net.minecraft.recipe.FireworkStarRecipe;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PillagerEntity.class)
public abstract class PillagerMixin extends IllagerEntity
        implements CrossbowUser,
        InventoryOwner {

    @Shadow public abstract State getState();

    protected PillagerMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "initEquipment", at=@At(value = "HEAD"), cancellable = true)
    public void fireworkRandomize(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        if (random.nextBetween(1, ModConfigs.PILLAGERFIREWORKSCHANCE) == 1 && ModConfigs.PILLAGERSFIREWORKSENABLED) {
            int flight = random.nextBetween(1, 3);
            ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 64);

            int howMany = random.nextBetween(1, 4);
            int howManyStars = random.nextBetween(1, 3);
            if (this.getWave() > 20) {
                howMany += 1;
                howManyStars += 2;
            }
            if (this.getWave() > 40) {
                howMany += 1;
                howManyStars += 1;
            }
            NbtList nbtList = new NbtList();
            NbtCompound nbtCompound2first = itemStack.getOrCreateSubNbt("Fireworks");
            for (int k = 0; k < howManyStars; k++) {
                ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);

                for (int i = 0; i < howMany; i++) {
                    NbtCompound nbtCompound = itemStack2.getOrCreateSubNbt("Explosion");

                    ArrayList<Integer> list = Lists.newArrayList();
                    DyeColor color = Util.getRandom(DyeColor.values(), random);
                    list.add(color.getFireworkColor());
                    nbtCompound.putIntArray("Colors", list);

                    int superChoose = random.nextBetween(1, 5);
                    if (superChoose == 1) {
                        nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.BURST.getId());
                    } else if (superChoose == 2) {
                        nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.CREEPER.getId());
                    } else if (superChoose == 3) {
                        nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.STAR.getId());
                    } else if (superChoose == 4) {
                        nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.LARGE_BALL.getId());
                    } else if (superChoose == 5) {
                        nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.SMALL_BALL.getId());
                    }

                    int fadeortwinkle = random.nextBetween(1, 10);

                    if (!list.isEmpty() && fadeortwinkle < 3) {
                        itemStack.getOrCreateSubNbt("Explosion").putIntArray("FadeColors", list);
                    }

                    if (!list.isEmpty() && fadeortwinkle > 7) {
                        nbtCompound.putBoolean("Flicker", true);
                    }


                    nbtList.add(nbtCompound);
                }

                NbtCompound nbtCompound3 = itemStack2.getSubNbt("Explosion");
                if (nbtCompound3 != null) {
                    nbtList.add(nbtCompound3);
                }
            }
            nbtCompound2first.putByte("Flight", (byte) flight);
            if (!nbtList.isEmpty()) {
                nbtCompound2first.put("Explosions", nbtList);
            }
            this.equipStack(EquipmentSlot.OFFHAND, itemStack);
        }
    }

    @ModifyConstant(method = "createPillagerAttributes", constant = @Constant(doubleValue = 32, ordinal = 0))
    private static double rangeExtentionMixin2(double constant) {
        return 48.0;
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(floatValue = 8.0f, ordinal = 0))
    private float rangeExtentionMixin(float constant) {
        return 20;
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(floatValue = 15.0f, ordinal = 0))
    private float rangeExtentionMixin2(float constant) {
        return 25;
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(floatValue = 15.0f, ordinal = 1))
    private float rangeExtentionMixin3(float constant) {
        return 25;
    }

    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.6f, ordinal = 0))
    private float rangeExtentionMixinspeed(float constant) {
        if (this.raid != null) {
            if (this.raid.waveCount > 16) {
                return 3.0f;
            }
        }
        return 1.6f;
    }


    @Inject(method = "initGoals", at=@At(value = "HEAD"))
    private void initGoalsMixin(CallbackInfo ci) {
        this.goalSelector.add(2, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 3.0f, 0.8, 1.0));
    }
}