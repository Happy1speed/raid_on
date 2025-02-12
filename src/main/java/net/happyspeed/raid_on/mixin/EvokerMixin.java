package net.happyspeed.raid_on.mixin;


import net.happyspeed.raid_on.config.ModConfigs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(EvokerEntity.class)
public abstract class EvokerMixin extends SpellcastingIllagerEntity {


    protected EvokerMixin(EntityType<? extends SpellcastingIllagerEntity> entityType, World world) {
        super(entityType, world);
    }


    public void onDeath(DamageSource source) {
        if (!this.getWorld().isClient() && ModConfigs.VEXESPERISHWITHEVOKERSENABLED) {
            List<VexEntity> list = this.getWorld().getNonSpectatingEntities(VexEntity.class, this.getBoundingBox().expand(100.0, 100.0, 100.0));
            for (VexEntity vexEntity : list) {
                if (vexEntity.getOwner() == this && !vexEntity.isDead()) {
                    vexEntity.kill();
                }
            }
        }
        super.onDeath(source);
    }
}
