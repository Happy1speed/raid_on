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

    @Redirect(method = "shoot(Lnet/minecraft/entity/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FFLnet/minecraft/entity/LivingEntity;)V"))
    private void noInaccuracy(CrossbowItem instance, World world, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float divergence, LivingEntity target) {
        instance.shootAll(shooter.getWorld(), shooter, hand, stack, speed, 0, target);
    }
}
