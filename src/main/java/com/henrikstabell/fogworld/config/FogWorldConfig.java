package com.henrikstabell.fogworld.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
public class FogWorldConfig {

    public static final ForgeConfigSpec CONFIG_SPEC;
    private static final FogWorldConfig CONFIG;

    public final ConfigValue fogDensity;
    public final IntValue fogColor;
    public final BooleanValue poisonousFog;
    public final IntValue poisonTicks;
    public final IntValue poisonDamage;


    static
    {
        Pair<FogWorldConfig,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(FogWorldConfig::new);

        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    FogWorldConfig(ForgeConfigSpec.Builder builder)
    {
        poisonousFog = builder
                .comment("Should the fog be poisonous?")
                .translation("config.fogworld.poisonousFog")
                .define("poisonousFog", false);
        fogColor = builder
                .comment("What color should the fog be? Set in decimal color.")
                .translation("config.fogworld.fogcolor")
                .defineInRange("fogColor", 16777215, 0, 16777215);
        fogDensity = builder
                .comment("What density should the fog have?")
                .translation("config.fogworld.fogdensity")
                .define("fogDensity", "0.1F");
        poisonTicks = builder
                .comment("How many ticks before the player takes damage in the fog? Minimum 20 (1 second), maximum 72000 (1 hour)")
                .translation("config.fogworld.poisonticks")
                .defineInRange("poisonTicks", 1200, 20, 72000);
        poisonDamage = builder
                .comment("How much damage should the poison fog deal per tick? Minimum 1, Maximum 20")
                .translation("config.fogworld.poisondamage")
                .defineInRange("poisonDamage", 1, 1, 20);
    }

    public static boolean poisonousFog() {
        return CONFIG.poisonousFog.get();
    }

    public static int fogColor() {
        return CONFIG.fogColor.get();
    }

    public static int poisonTicks() {
        return CONFIG.poisonTicks.get();
    }

    public static int poisonDamage() {
        return CONFIG.poisonDamage.get();
    }

    public static Float getFogDensity() {
        return Float.parseFloat(CONFIG.fogDensity.get().toString());
    }

}
