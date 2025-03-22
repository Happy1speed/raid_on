package net.happyspeed.raid_on.mixin;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.happyspeed.raid_on.config.ModConfigs;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

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
        if (random.nextBetween(1, ModConfigs.PILLAGERFIREWORKSCHANCE) == 1 && ModConfigs.PILLAGERFIREWORKSENABLED) {
            //Random Flight Durration
            int flight = random.nextBetween(1, 3);

            int howMany = random.nextBetween(2, 7);
            //Random amount of Stars
            if (this.getWave() > 20) {
                howMany += 2;
            }
            if (this.getWave() > 40) {
                howMany += 3;
            }
            //Increment Stars based on Factors


            //Firework Creation
            List<FireworkExplosionComponent> Explosionslist = new ArrayList();
            for (int i = 0; i < howMany; i++) {
                FireworkExplosionComponent.Type type = FireworkExplosionComponent.Type.SMALL_BALL;
                boolean twinkle = false;
                //Loop Star Explosions

                IntList intList = new IntArrayList();
                IntList intList2 = new IntArrayList();
                //Random Dye Color
                DyeColor color = Util.getRandom(DyeColor.values(), random);
                intList.add(color.getFireworkColor());

                //Random Star Shape

                int superChoose = random.nextBetween(1, 5);
                if (superChoose == 1) {
                    type = FireworkExplosionComponent.Type.BURST;
                } else if (superChoose == 2) {
                    type = FireworkExplosionComponent.Type.CREEPER;
                } else if (superChoose == 3) {
                    type = FireworkExplosionComponent.Type.STAR;
                } else if (superChoose == 4) {
                    type = FireworkExplosionComponent.Type.LARGE_BALL;
                }


                //Trail or Twinkle
                int Doestwinkle = random.nextBetween(1, 5);

                if (Doestwinkle < 2) {
                    twinkle = true;
                }

                if (random.nextBetween(1, 2) < 2) {
                    DyeColor colorFade = Util.getRandom(DyeColor.values(), random);
                    intList2.add(colorFade.getFireworkColor());
                }

                Explosionslist.add(new FireworkExplosionComponent(type, intList, intList2, false, twinkle));
            }

            ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 64);
            itemStack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(flight, Explosionslist));

            //Equip Stack
            this.equipStack(EquipmentSlot.OFFHAND, itemStack);
            this.setEquipmentDropChance(EquipmentSlot.OFFHAND, 0.0f);
        }
    }

    @ModifyConstant(method = "createPillagerAttributes", constant = @Constant(doubleValue = 32, ordinal = 0))
    private static double rangeExtentionMixin2(double constant) {
        if (ModConfigs.PILLAGERLARGERVIEWRANGE) {
            return 48.0;
        }
        return constant;
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(floatValue = 8.0f, ordinal = 0))
    private float rangeExtentionMixin(float constant) {
        if (ModConfigs.PILLAGERLARGERVIEWRANGE) {
            return 20;
        }
        return constant;
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(floatValue = 15.0f, ordinal = 0))
    private float rangeExtentionMixin2(float constant) {
        if (ModConfigs.PILLAGERLARGERVIEWRANGE) {
            return 25;
        }
        return constant;
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(floatValue = 15.0f, ordinal = 1))
    private float rangeExtentionMixin3(float constant) {
        if (ModConfigs.PILLAGERLARGERVIEWRANGE) {
            return 25;
        }
        return constant;
    }

    @ModifyConstant(method = "shootAt", constant = @Constant(floatValue = 1.6F, ordinal = 0))
    private float reAim(float constant) {
        if (this.raid != null && ModConfigs.PILLAGERLARGERVIEWRANGE) {
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