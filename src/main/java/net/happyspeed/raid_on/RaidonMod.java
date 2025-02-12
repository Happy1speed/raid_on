package net.happyspeed.raid_on;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.happyspeed.raid_on.config.ModConfigs;
import net.happyspeed.raid_on.item.ModItemGroups;
import net.happyspeed.raid_on.item.ModItems;
import net.happyspeed.raid_on.status_effects.RaidWaveLootEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.raid.Raid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

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


		//raid start
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_start")
				.then(argument("level", IntegerArgumentType.integer())
						.executes(context -> StartRaid(IntegerArgumentType.getInteger(context, "level"), context)))));


		//raid end
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_end")
				.executes(context -> {
					if (context.getSource().getEntity() instanceof PlayerEntity player) {
						if (player.getWorld() instanceof ServerWorld serverWorld) {
							Raid raid = serverWorld.getRaidAt(player.getBlockPos());
							if (raid != null) {
								raid.invalidate();
								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.ended_raid"), false);
								return 1;
							}
						}
					}
					context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.could_not_end_raid"), false);
					return 0;
				})));


		//raid set wave
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_set_wave")
				.then(argument("wave", IntegerArgumentType.integer())
						.executes(context -> SetRaidWave(IntegerArgumentType.getInteger(context, "wave"), context)))));



		//raid kill all raiders
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_kill_all_raiders")
				.executes(context -> {
					if (context.getSource().getEntity() instanceof PlayerEntity player) {
						if (player.getWorld() instanceof ServerWorld serverWorld) {
							Raid raid = serverWorld.getRaidAt(player.getBlockPos());
							if (raid != null) {
								for (RaiderEntity raiderEntity : raid.getAllRaiders()) {
									raiderEntity.kill();
								}
								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_killed_raiders"), false);
								return 1;
							}
						}
					}
					context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_killed_raiders_fail"), false);
					return 0;
				})));

		//raid terminate
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_terminate")
				.executes(context -> {
					if (context.getSource().getEntity() instanceof PlayerEntity player) {
						if (player.getWorld() instanceof ServerWorld serverWorld) {
							Raid raid = serverWorld.getRaidAt(player.getBlockPos());
							if (raid != null) {
								for (RaiderEntity raiderEntity : raid.getAllRaiders()) {
									raiderEntity.kill();
								}
								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_killed_raiders"), false);

								raid.invalidate();

								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_termination_success"), false);
								return 1;
							}
						}
					}
					context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_termination_fail"), false);
					return 0;
				})));



	}

	private static int SetRaidWave(int wave, CommandContext<ServerCommandSource> context) {
		final int result = wave;
		if (context.getSource().getEntity() instanceof PlayerEntity player) {
			if (player.getWorld() instanceof ServerWorld serverWorld) {
				Raid raid = serverWorld.getRaidAt(player.getBlockPos());
				if (raid != null) {
					raid.wavesSpawned = wave;
					context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_wave_set").append("%s".formatted(wave)), false);
					return result;
				}
			}
		}
		context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_wave_set_fail"), false);
		return result;
	}

	private static int StartRaid(int level, CommandContext<ServerCommandSource> context) {
		if (context.getSource().getEntity() instanceof PlayerEntity player) {
			if (player.getWorld() instanceof ServerWorld) {
				player.setStatusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 20, level, false, false), player);
				context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_start").append("%s".formatted(level)), false);
				return 1;
			}
		}
		return 0;
	}
}