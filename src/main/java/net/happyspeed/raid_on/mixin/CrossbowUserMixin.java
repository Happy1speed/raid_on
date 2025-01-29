package net.happyspeed.raid_on.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CrossbowUser.class)
public interface CrossbowUserMixin
        extends RangedAttackMob {

    @Redirect(method = "shoot(Lnet/minecraft/entity/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V"))
    private void noInaccuracy(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
        CrossbowItem.shootAll(entity.getWorld(), entity, hand, stack, speed, 0);
    }

    @Redirect(method = "shoot(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/projectile/ProjectileEntity;FF)V", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"))
    private double fixFireworkLaunch(double a, @Local(ordinal = 0) ProjectileEntity projectile) {
        if (projectile instanceof FireworkRocketEntity) {
            return Math.sqrt(a) * 0.2;
        }
        return Math.sqrt(a);
    }
}
