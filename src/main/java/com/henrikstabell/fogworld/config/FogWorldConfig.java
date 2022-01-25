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

    public final IntValue fogDensity;
    public final IntValue fogColorRed;
    public final IntValue fogColorGreen;
    public final IntValue fogColorBlue;
    public final IntValue poisonTicks;
    public final IntValue poisonDamage;
    public final BooleanValue poisonousFog;

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
        fogDensity = builder
                .comment("Controls the amount/density of the fog.")
                .translation("config.fogworld.fogdensity")
                .defineInRange("fogDensity", 3, 1, 300);
        fogColorRed = builder
                .comment("Set the RED color value for the fog. Minimum 0, maximum 255")
                .translation("config.fogworld.fogcolorred")
                .defineInRange("fogColorRed", 0, 0, 255);
        fogColorGreen = builder
                .comment("Set the GREEN color value for the fog. Minimum 0, maximum 255")
                .translation("config.fogworld.fogcolorgreen")
                .defineInRange("fogColorGreen", 0, 0, 255);
        fogColorBlue = builder
                .comment("Set the BLUE color value for the fog. Minimum 0, maximum 255")
                .translation("config.fogworld.fogcolorblue")
                .defineInRange("fogColorBlue", 0, 0, 255);
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

    public static float fogColorRed() {
        return CONFIG.fogColorRed.get().floatValue();
    }

    public static float fogColorGreen() {
        return CONFIG.fogColorGreen.get().floatValue();
    }

    public static float fogColorBlue() {
        return CONFIG.fogColorBlue.get().floatValue();
    }

    public static int poisonTicks() {
        return CONFIG.poisonTicks.get();
    }

    public static int poisonDamage() {
        return CONFIG.poisonDamage.get();
    }

    public static Float getFogDensity() {
        return CONFIG.fogDensity.get().floatValue();
    }

}
