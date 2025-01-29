package net.happyspeed.raid_on.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.happyspeed.raid_on.RaidonMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup RAIDON_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(RaidonMod.MOD_ID, "raidon"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.raidon"))
                    .icon(() -> new ItemStack(ModItems.PRISMOFSIGHT)).entries((displayContext, entries) -> {
                        entries.add(ModItems.PRISMOFSIGHT);
                        entries.add(ModItems.BOTTLEOOMEN);
                        entries.add(ModItems.RAID_STONE);

                    }).build());


    public static void registerItemGroups() {
        RaidonMod.LOGGER.info("Registering Item Groups for " + RaidonMod.MOD_ID);
    }
}
