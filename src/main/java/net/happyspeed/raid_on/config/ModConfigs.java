package net.happyspeed.raid_on.config;

import com.mojang.datafixers.util.Pair;
import net.happyspeed.raid_on.RaidonMod;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static boolean SCALESTATUSEFFECTS;
    public static boolean ACTIVEWITCHES;
    public static double RAIDWAVESCALEAMOUNT;
    public static double TOTALWAVESCALEFACTOR;
    public static int SLOWWAVETIMER;
    public static int FASTWAVETIMER;
    public static int PILLAGERFIREWORKSCHANCE;
    public static boolean PILLAGERSFIREWORKSENABLED;
    public static boolean VEXESPERISHWITHEVOKERSENABLED;
    public static boolean WITCHESCANTHROWLINGERINGENABLED;
    public static boolean NORAIDERFRIENDLYFIRE;
    public static int MAXOMENLEVELCOMMANDS;
    public static int MAXOMENLEVELNATURAL;
    public static boolean HEALTHSTATUSEFFECTS;
    public static boolean RAIDSTATUSEFFECTS;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(RaidonMod.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("scale_status_effects", false));
        configs.addKeyValuePair(new Pair<>("health_status_effects", true));
        configs.addKeyValuePair(new Pair<>("raid_status_effects", true));
        configs.addKeyValuePair(new Pair<>("active_witches", true));
        configs.addKeyValuePair(new Pair<>("raiders_in_wave_scale_amount", 0.2));
        configs.addKeyValuePair(new Pair<>("total_wave_scale_factor", 1.2));
        configs.addKeyValuePair(new Pair<>("slow_wave_timer", 300));
        configs.addKeyValuePair(new Pair<>("fast_wave_timer", 20));
        configs.addKeyValuePair(new Pair<>("pillager_fireworks_chance", 10));
        configs.addKeyValuePair(new Pair<>("pillager_fireworks_enabled", true));
        configs.addKeyValuePair(new Pair<>("vexes_perish_with_evokers_enabled", true));
        configs.addKeyValuePair(new Pair<>("witches_can_throw_lingering_enabled", true));
        configs.addKeyValuePair(new Pair<>("no_raider_friendly_fire", true));
        configs.addKeyValuePair(new Pair<>("max_omen_level_commands", 255));
        configs.addKeyValuePair(new Pair<>("max_omen_level_natural", 50));
    }

    private static void assignConfigs() {
        SCALESTATUSEFFECTS = CONFIG.getOrDefault("scale_status_effects", false);
        HEALTHSTATUSEFFECTS = CONFIG.getOrDefault("health_status_effects", true);
        RAIDSTATUSEFFECTS = CONFIG.getOrDefault("raid_status_effects", true);
        ACTIVEWITCHES = CONFIG.getOrDefault("active_witches", true);
        RAIDWAVESCALEAMOUNT = CONFIG.getOrDefault("raiders_in_wave_scale_amount", 0.2);
        TOTALWAVESCALEFACTOR = CONFIG.getOrDefault("total_wave_scale_factor", 1.2);
        SLOWWAVETIMER = CONFIG.getOrDefault("slow_wave_timer", 300);
        FASTWAVETIMER = CONFIG.getOrDefault("fast_wave_timer", 20);
        PILLAGERFIREWORKSCHANCE = CONFIG.getOrDefault("pillager_fireworks_chance", 10);
        PILLAGERSFIREWORKSENABLED = CONFIG.getOrDefault("pillager_fireworks_enabled", true);
        VEXESPERISHWITHEVOKERSENABLED = CONFIG.getOrDefault("vexes_perish_with_evokers_enabled", true);
        WITCHESCANTHROWLINGERINGENABLED = CONFIG.getOrDefault("witches_can_throw_lingering_enabled", true);
        NORAIDERFRIENDLYFIRE = CONFIG.getOrDefault("no_raider_friendly_fire", true);
        MAXOMENLEVELCOMMANDS = CONFIG.getOrDefault("max_omen_level_commands", 255);
        MAXOMENLEVELNATURAL = CONFIG.getOrDefault("max_omen_level_natural", 50);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}