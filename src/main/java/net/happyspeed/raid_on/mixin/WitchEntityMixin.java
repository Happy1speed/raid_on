package net.happyspeed.raid_on.mixin;


import net.happyspeed.raid_on.config.ModConfigs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity {

    @Shadow public abstract boolean isDrinking();

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }


    @ModifyConstant(method = "initGoals", constant = @Constant(intValue = 60))
    private int modAttackinter(int constant) {
        if (ModConfigs.ACTIVEWITCHES) {
            return 40;
        }
        else {
            return 60;
        }
    }

    @Redirect(method = "attack", at= @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionUtil;setPotion(Lnet/minecraft/item/ItemStack;Lnet/minecraft/potion/Potion;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack redirectLingering(ItemStack stack, Potion potion) {
        if (this.hasActiveRaid() && !(this.getTarget() instanceof RaiderEntity) && !this.isDrinking()) {
            if (this.random.nextBetween(1, 5) == 1) {
                return PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
            }
        }
        stack = PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
        return stack;
    }
}
