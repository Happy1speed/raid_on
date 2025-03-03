package net.happyspeed.raid_on.mixin;

import net.happyspeed.raid_on.RaidonMod;
import net.happyspeed.raid_on.config.ModConfigs;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow private int badOmenLevel;

    @Shadow public abstract World getWorld();

    @Mutable
    @Shadow @Final public int waveCount;

    @Shadow public abstract int getBadOmenLevel();

    @Shadow private int wavesSpawned;

    @Shadow @Final private ServerBossBar bar;

    @Shadow @Final private static Text EVENT_TEXT;

    @Shadow public abstract int getRaiderCount();

    @Shadow @Final private Random random;

    @Shadow public abstract int getMaxWaves(Difficulty difficulty);

    @Shadow protected abstract boolean hasExtraWave();

    @Inject(method = "getMaxWaves", at=@At(value = "HEAD"), cancellable = true)
    public void waveLevelsScaleWavesMixin(Difficulty difficulty, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(800);
        cir.cancel();
    }

    @Inject(method = "getMaxAcceptableBadOmenLevel", at=@At(value = "HEAD"), cancellable = true)
    public void waveLevelsScalingOmen(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ModConfigs.MAXOMENLEVELCOMMANDS);
        cir.cancel();
    }

    @Inject(method = "start", at=@At(value = "TAIL"))
    public void waveLevelsScaleMixin2(PlayerEntity player, CallbackInfo ci) {
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            this.waveCount = 0;
        }
        else if (this.badOmenLevel >= 255) {
            this.waveCount = 99999999;
        }
        else {
            this.waveCount = (int) (((this.badOmenLevel + 1) * (float) ModConfigs.TOTALWAVESCALEFACTOR) + 1) + 2;
        }
    }

    @Inject(method = "getEnchantmentChance", at=@At(value = "HEAD"), cancellable = true)
    public void enchantmentScaleOmen(CallbackInfoReturnable<Float> cir) {
        if (this.getBadOmenLevel() > 5) {
            cir.setReturnValue(1.0f);
        }
    }

    @Inject(method = "getCount", at=@At(value = "HEAD"), cancellable = true)
    public void enchantmentENUMFixScalingCount(Raid.Member member, int wave, boolean extra, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((extra ? member.countInWave[Math.min(7, this.waveCount)] : member.countInWave[Math.min(7, wave)]));
        cir.cancel();
    }

    @Inject(method = "addToWave(ILnet/minecraft/entity/raid/RaiderEntity;Z)Z", at=@At(value = "HEAD"), cancellable = true)
    public void raiderEffectsScale(int wave, RaiderEntity entity, boolean countHealth, CallbackInfoReturnable<Boolean> cir) {
        if (wave > 9) {
            if (ModConfigs.RAIDSTATUSEFFECTS) {
                if (entity instanceof VindicatorEntity) {
                    if (ModConfigs.SCALESTATUSEFFECTS) {
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, StatusEffectInstance.INFINITE, (int) (wave * 0.4f), false, false), entity);
                    } else {
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, StatusEffectInstance.INFINITE, 0, false, false), entity);
                    }
                } else if (entity instanceof PillagerEntity) {
                    if (ModConfigs.SCALESTATUSEFFECTS) {
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, StatusEffectInstance.INFINITE, (int) Math.min(wave * 0.5f, 5), false, false), entity);
                    } else {
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, StatusEffectInstance.INFINITE, 0, false, false), entity);
                    }
                } else if (entity instanceof EvokerEntity) {
                    entity.setStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, StatusEffectInstance.INFINITE, 6, false, false), entity);
                } else if (entity instanceof RavagerEntity) {
                    entity.setStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, StatusEffectInstance.INFINITE, 1, false, false), entity);
                }
                if (ModConfigs.HEALTHSTATUSEFFECTS) {
                    if (ModConfigs.SCALESTATUSEFFECTS) {
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, StatusEffectInstance.INFINITE, (int) (wave * 0.2f) + 1, false, false), entity);
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 40, 6, false, false), entity);
                    } else {
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, StatusEffectInstance.INFINITE, 1, false, false), entity);
                        entity.setStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 40, 6, false, false), entity);
                    }
                }

                if (this.random.nextBetween(1, 4) < 2 && wave % 10 == 0) {
                    entity.setStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, StatusEffectInstance.INFINITE, 1, false, false), entity);
                }
            }
        }
        entity.setStatusEffect(new StatusEffectInstance(RaidonMod.RAID_WAVE_LOOT_EFFECT, StatusEffectInstance.INFINITE, wave, false, false), entity);
    }

    @Inject(method = "getBonusCount", at=@At(value = "HEAD"), cancellable = true)
    public void getMoreRaiders(Raid.Member member, Random random, int wave, LocalDifficulty localDifficulty, boolean extra, CallbackInfoReturnable<Integer> cir) {
        int i = 0;
        if (this.wavesSpawned > 5) {
            i = (int) (this.wavesSpawned * (float) (ModConfigs.RAIDWAVESCALEAMOUNT));
            switch (member) {
                case WITCH: {
                    if (wave % 4 == 0) {
                        i = random.nextBetween(i, i + 10);
                        break;
                    }
                    if (random.nextBetween(1, 30) < 2) {
                        i = this.wavesSpawned + 1;
                        break;
                    }
                    cir.setReturnValue(0);

                }
                case PILLAGER:
                    if (wave % 2 == 0) {
                        i = random.nextBetween(i, i + 15);
                        break;
                    }
                    if (random.nextBetween(1, 10) < 2) {
                        i = this.wavesSpawned + 1;
                        break;
                    }
                    break;
                case VINDICATOR: {
                    if (wave % 3 == 0) {
                        i = random.nextBetween(i, i + 10);
                        break;
                    }
                    if (random.nextBetween(1, 18) < 2) {
                        i = this.wavesSpawned + 1;
                        break;
                    }
                    break;
                }
                case RAVAGER: {
                    if ((wave % 5 == 0 || wave % 7 == 0) && wave > 6) {
                        i = extra ? random.nextBetween(i, i + 1) : 0;
                    }
                    break;
                }
                case EVOKER: {
                    if (wave % 5 == 0) {
                        i = random.nextBetween((int) (1 + i * 0.1f), (int) (1 + i * 0.3f));
                        break;
                    }
                    if (random.nextBetween(1, 35) < 2) {
                        i = (int) ((this.wavesSpawned + 1) * 0.3f);
                        break;
                    }
                    cir.setReturnValue(1);

                }
                default: {
                    if (random.nextBetween(1, 20) < 2) {
                        i = this.wavesSpawned + 1;
                        break;
                    }
                    cir.setReturnValue(i);
                }
            }
        }
        cir.setReturnValue(i);
        cir.cancel();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/ServerBossBar;setName(Lnet/minecraft/text/Text;)V", ordinal = 2))
    public void waveDisplay1(ServerBossBar instance, Text name) {
        if (this.wavesSpawned != 0) {
            this.bar.setName(EVENT_TEXT.copy().append(" | ").append(Text.translatable("event.raid_on.show_waves_remaining", new Object[]{this.wavesSpawned})));
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/ServerBossBar;setName(Lnet/minecraft/text/Text;)V", ordinal = 3))
    public void waveDisplay3(ServerBossBar instance, Text name) {
        if (this.wavesSpawned != 0) {
            this.bar.setName(EVENT_TEXT.copy().append(" | ").append(Text.translatable("event.raid_on.show_waves_remaining", new Object[]{this.wavesSpawned})));
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/ServerBossBar;setName(Lnet/minecraft/text/Text;)V", ordinal = 1))
    public void waveDisplay2(ServerBossBar instance, Text name) {
        if (this.badOmenLevel >= 255) {
            if (this.waveCount < 9000) {
                this.waveCount += 1;
            }

        }
        int i = this.getRaiderCount();
        this.bar.setName(EVENT_TEXT.copy().append(" - ").append(Text.translatable("event.minecraft.raid.raiders_remaining", new Object[]{i})).append(" | ").append(Text.translatable("event.raid_on.show_waves_remaining", new Object[]{this.wavesSpawned})));
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 2, ordinal = 1))
    private int modifyDisplayRaiderCount(int constant) {
        return (int) (this.wavesSpawned * 1.2f) + 2;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 300, ordinal = 0))
    private int modifyraidtime2(int constant) {
        return this.wavesSpawned >= 5 ? ModConfigs.FASTWAVETIMER : ModConfigs.SLOWWAVETIMER;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 100, ordinal = 0))
    private int modifyraidtime3(int constant) {
        return this.wavesSpawned >= 5 ? (int) (ModConfigs.FASTWAVETIMER * 0.3f) : (int) (ModConfigs.SLOWWAVETIMER * 0.3f);
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 40, ordinal = 0))
    private int modifyraidtime4(int constant) {
        return this.wavesSpawned >= 5 ? (int) (ModConfigs.FASTWAVETIMER * 0.13f) : (int) (ModConfigs.SLOWWAVETIMER * 0.13f);
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 300, ordinal = 1))
    private int modifyraidtime5(int constant) {
        return this.wavesSpawned >= 5 ? ModConfigs.FASTWAVETIMER : ModConfigs.SLOWWAVETIMER;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 300, ordinal = 2))
    private int modifyraidtime6(int constant) {
        return this.wavesSpawned >= 5 ? ModConfigs.FASTWAVETIMER : ModConfigs.SLOWWAVETIMER;
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 300f, ordinal = 0))
    private float modifyraidtime7(float constant) {
        return this.wavesSpawned >= 5 ? (float) ModConfigs.FASTWAVETIMER : (float) ModConfigs.SLOWWAVETIMER;
    }

    @ModifyConstant(method = "tick", constant = @Constant(longValue = 48000L, ordinal = 0))
    private long modifyraidMaxTime(long constant) {
        return (12000L * this.waveCount);
    }


}