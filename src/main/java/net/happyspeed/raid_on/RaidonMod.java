package net.happyspeed.raid_on;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.happyspeed.raid_on.config.ModConfigs;
import net.happyspeed.raid_on.item.ModItemGroups;
import net.happyspeed.raid_on.item.ModItems;
import net.happyspeed.raid_on.status_effects.RaidWaveLootEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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
				.requires(source -> source.hasPermissionLevel(2))
				.then(argument("level", IntegerArgumentType.integer())
						.executes(context -> StartRaid(IntegerArgumentType.getInteger(context, "level"), context)))));


		//raid end
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_end")
				.requires(source -> source.hasPermissionLevel(2))
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
				.requires(source -> source.hasPermissionLevel(2))
				.then(argument("wave", IntegerArgumentType.integer())
						.executes(context -> SetRaidWave(IntegerArgumentType.getInteger(context, "wave"), context)))));



		//raid kill all raiders
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_kill_all_raiders")
				.requires(source -> source.hasPermissionLevel(2))
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

		//raid despawn all raiders
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_despawn_all_raiders")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> {
					if (context.getSource().getEntity() instanceof PlayerEntity player) {
						if (player.getWorld() instanceof ServerWorld serverWorld) {
							Raid raid = serverWorld.getRaidAt(player.getBlockPos());
							if (raid != null) {
								for (RaiderEntity raiderEntity : raid.getAllRaiders()) {
									raiderEntity.discard();
								}
								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_despawned_raiders"), false);
								return 1;
							}
						}
					}
					context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.could_not_find_raid"), false);
					return 0;
				})));

		//raid terminate
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_terminate")
				.requires(source -> source.hasPermissionLevel(2))
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

		//raid terminate quietly
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_terminate_quietly")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> {
					if (context.getSource().getEntity() instanceof PlayerEntity player) {
						if (player.getWorld() instanceof ServerWorld serverWorld) {
							Raid raid = serverWorld.getRaidAt(player.getBlockPos());
							if (raid != null) {
								for (RaiderEntity raiderEntity : raid.getAllRaiders()) {
									raiderEntity.discard();
								}
								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_despawned_raiders"), false);

								raid.invalidate();

								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_termination_success"), false);
								return 1;
							}
						}
					}
					context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_termination_fail"), false);
					return 0;
				})));


		//raid debug info
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_debug_info")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> {
					if (context.getSource().getEntity() instanceof PlayerEntity player) {
						if (player.getWorld() instanceof ServerWorld serverWorld) {
							Raid raid = serverWorld.getRaidAt(player.getBlockPos());
							if (raid != null) {
								context.getSource().sendFeedback(() -> Text.translatable("message.raid_on.currentraidercount").append(String.valueOf(raid.getRaiderCount())), false);
								context.getSource().sendFeedback(() -> Text.translatable("message.raid_on.wavescount").append(String.valueOf(raid.waveCount + (raid.hasExtraWave() ? 1 : 0))), false);
								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_current_wave").append(String.valueOf(raid.wavesSpawned)), false);
								context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.raid_current_time").append(String.valueOf((12000L * raid.waveCount) - raid.ticksActive)), false);
								return 1;
							}
						}
					}
					context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.could_not_find_raid"), false);
					return 0;
				})));
		//raid debug test fireworks
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("raid_debug_test_fireworks")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> {
					if (context.getSource().getEntity() instanceof LivingEntity entity) {
						if (entity.getWorld() instanceof ServerWorld serverWorld) {
							Random random = entity.random;
							int flight = random.nextBetween(1, 3);
							ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 64);

							int howMany = entity.random.nextBetween(1, 4);
							int howManyStars = random.nextBetween(1, 3);
							Raid raid = serverWorld.getRaidAt(entity.getBlockPos());
							if (raid != null) {
								if (raid.wavesSpawned > 20) {
									howMany += 1;
									howManyStars += 2;
								}
								if (raid.wavesSpawned > 40) {
									howMany += 1;
									howManyStars += 1;
								}
							}
							NbtList nbtList = new NbtList();
							NbtCompound nbtCompound2first = itemStack.getOrCreateSubNbt("Fireworks");
							for (int k = 0; k < howManyStars; k++) {
								ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);

								for (int i = 0; i < howMany; i++) {
									NbtCompound nbtCompound = itemStack2.getOrCreateSubNbt("Explosion");

									ArrayList<Integer> list = Lists.newArrayList();
									DyeColor color = Util.getRandom(DyeColor.values(), random);
									list.add(color.getFireworkColor());
									nbtCompound.putIntArray("Colors", list);

									int superChoose = random.nextBetween(1, 5);
									if (superChoose == 1) {
										nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.BURST.getId());
									} else if (superChoose == 2) {
										nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.CREEPER.getId());
									} else if (superChoose == 3) {
										nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.STAR.getId());
									} else if (superChoose == 4) {
										nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.LARGE_BALL.getId());
									} else if (superChoose == 5) {
										nbtCompound.putByte("Type", (byte) FireworkRocketItem.Type.SMALL_BALL.getId());
									}

									int fadeortwinkle = random.nextBetween(1, 10);

									if (!list.isEmpty() && fadeortwinkle < 3) {
										itemStack.getOrCreateSubNbt("Explosion").putIntArray("FadeColors", list);
									}

									if (!list.isEmpty() && fadeortwinkle > 7) {
										nbtCompound.putBoolean("Flicker", true);
									}


									nbtList.add(nbtCompound);
								}

								NbtCompound nbtCompound3 = itemStack2.getSubNbt("Explosion");
								if (nbtCompound3 != null) {
									nbtList.add(nbtCompound3);
								}
							}
							nbtCompound2first.putByte("Flight", (byte) flight);
							if (!nbtList.isEmpty()) {
								nbtCompound2first.put("Explosions", nbtList);
							}
							entity.equipStack(EquipmentSlot.OFFHAND, itemStack);
							context.getSource().sendFeedback(() -> Text.translatable("commands.raid_on.gen_fireworks"), false);
							return 1;
						}
					}
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