package net.happyspeed.raid_on.mixin;

import net.happyspeed.raid_on.config.ModConfigs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VexEntity.class)
public abstract class VexMixin extends HostileEntity implements Ownable {

    protected VexMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "initEquipment", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/VexEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"), index = 1)
    public ItemStack editVexMainhandItem(ItemStack par2) {
        if (ModConfigs.REMOVEVEXMELEEWEAPON) {
            return new ItemStack(Items.AIR);
        }
        return par2;
    }
}
