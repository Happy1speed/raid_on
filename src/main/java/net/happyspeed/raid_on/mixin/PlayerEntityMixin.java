package net.happyspeed.raid_on.mixin;


import net.happyspeed.raid_on.item.custom.RaidOMeter;
import net.happyspeed.raid_on.item.custom.SightPrism;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at=@At(value = "HEAD"))
    public void tickHeadMix(CallbackInfo ci) {
        if (this.getMainHandStack().getItem() instanceof SightPrism sightPrism) {
            sightPrism.activeAbility(this.getWorld(), this);
        }
        if (this.getMainHandStack().getItem() instanceof RaidOMeter raidOMeter) {
            raidOMeter.doThing(this.getMainHandStack(),this.getWorld(), this);
        }
    }
}
