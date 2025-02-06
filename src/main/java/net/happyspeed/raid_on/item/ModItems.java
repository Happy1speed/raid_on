package net.happyspeed.raid_on.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.happyspeed.raid_on.RaidonMod;
import net.happyspeed.raid_on.item.custom.OmenUp;
import net.happyspeed.raid_on.item.custom.RaidOMeter;
import net.happyspeed.raid_on.item.custom.SightPrism;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItems {

    public static final Item PRISMOFSIGHT = registerItem("prism_of_sight",
            new SightPrism(new FabricItemSettings().maxCount(1)));

    public static final Item BOTTLEOOMEN = registerItem("bottle_o_omen",
            new OmenUp(new FabricItemSettings().maxCount(64)));

    public static final Item RAID_STONE = registerItem("raid_stone",
            new RaidOMeter(new FabricItemSettings().maxCount(1)));


    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(PRISMOFSIGHT);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(RaidonMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        RaidonMod.LOGGER.info("Registering Mod Items for " + RaidonMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);

    }
}
