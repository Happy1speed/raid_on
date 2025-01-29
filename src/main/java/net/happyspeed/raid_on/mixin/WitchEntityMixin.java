package net.happyspeed.raid_on.mixin;


import net.happyspeed.raid_on.RaidonMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity {

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(intValue = 60))
    private int modAttackinter(int constant) {
        return 40;
    }
}
