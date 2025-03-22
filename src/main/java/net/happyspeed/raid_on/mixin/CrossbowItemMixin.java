package net.happyspeed.raid_on.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends RangedWeaponItem {

    public CrossbowItemMixin(Settings settings) {
        super(settings);
    }

    @Redirect(method = "shoot", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"))
    private double fixFireworkLaunch(double a, @Local(ordinal = 0) ProjectileEntity projectile) {
        if (projectile instanceof FireworkRocketEntity) {
            return Math.sqrt(a) * 0.2;
        }
        return Math.sqrt(a);
    }
}
