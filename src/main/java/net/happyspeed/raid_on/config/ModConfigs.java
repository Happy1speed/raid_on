package net.happyspeed.raid_on.config;

import com.mojang.datafixers.util.Pair;
import net.happyspeed.raid_on.RaidonMod;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static boolean SCALESTATUSEFFECTS;
    public static boolean ACTIVEWITCHES;
    public static double RAIDWAVESCALEAMOUNT;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(RaidonMod.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("scalestatuseffects", false));
        configs.addKeyValuePair(new Pair<>("activewitches", true));
        configs.addKeyValuePair(new Pair<>("raidwavescaleamount", 0.2));
    }

    private static void assignConfigs() {
        SCALESTATUSEFFECTS = CONFIG.getOrDefault("scalestatuseffects", false);
        ACTIVEWITCHES = CONFIG.getOrDefault("activewitches", true);
        RAIDWAVESCALEAMOUNT = CONFIG.getOrDefault("raidwavescaleamount", 0.2);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}