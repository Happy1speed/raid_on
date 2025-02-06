package net.happyspeed.raid_on;

import net.fabricmc.api.ModInitializer;

import net.happyspeed.raid_on.config.ModConfigs;
import net.happyspeed.raid_on.item.ModItemGroups;
import net.happyspeed.raid_on.item.ModItems;
import net.happyspeed.raid_on.status_effects.RaidWaveLootEffect;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaidonMod implements ModInitializer {
	public static final String MOD_ID = "raid_on";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static StatusEffect RAID_WAVE_LOOT_EFFECT = new RaidWaveLootEffect();

	@Override
	public void onInitialize() {
		LOGGER.info("Raid on my bros!");

		ModConfigs.registerConfigs();

		Registry.register(Registries.STATUS_EFFECT, new Identifier("raid_on", "raid_wave_loot_effect"), RAID_WAVE_LOOT_EFFECT);

		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
	}
}