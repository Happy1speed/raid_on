package net.happyspeed.raid_on.mixin;


import net.happyspeed.raid_on.config.ModConfigs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RaiderEntity.class)
public abstract class RaiderEntityMixin extends PatrolEntity {

    protected RaiderEntityMixin(EntityType<? extends PatrolEntity> entityType, World world) {
        super(entityType, world);
    }

    protected float modifyAppliedDamage(DamageSource source, float amount) {
        if (ModConfigs.NORAIDERFRIENDLYFIRE) {
            amount = super.modifyAppliedDamage(source, amount);
            if (source.getAttacker() == this) {
                amount = 0.0F;
            }

            if (source.getAttacker() instanceof RaiderEntity) {
                amount = 0.0F;
            }
        }

        return amount;
    }
}
