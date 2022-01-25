package com.henrikstabell.fogworld.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
public class Configuration {

    public static class Client {

        public final IntValue fogDensity;
        public final IntValue fogColorRed;
        public final IntValue fogColorGreen;
        public final IntValue fogColorBlue;

        public Client(ForgeConfigSpec.Builder builder) {
            fogDensity = builder
                    .comment("Controls the amount/density of the fog.")
                    .translation("config.fogworld.fogdensity")
                    .defineInRange("fogDensity", 3, 1, 600);
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
        }

    }

    public static class Common {

        public final IntValue poisonTicks;
        public final IntValue poisonDamage;
        public final BooleanValue poisonousFog;

        public Common(ForgeConfigSpec.Builder builder) {
            poisonousFog = builder
                    .comment("Should the fog be poisonous?")
                    .translation("config.fogworld.poisonousFog")
                    .define("poisonousFog", false);
            poisonTicks = builder
                    .comment("How many ticks before the player takes damage in the fog? Minimum 20 (1 second), maximum 72000 (1 hour)")
                    .translation("config.fogworld.poisonticks")
                    .defineInRange("poisonTicks", 1200, 20, 72000);
            poisonDamage = builder
                    .comment("How much damage should the poison fog deal per tick? Minimum 1, Maximum 20")
                    .translation("config.fogworld.poisondamage")
                    .defineInRange("poisonDamage", 1, 1, 20);
        }

    }

    public static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static
    {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Common, ForgeConfigSpec> testSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = testSpecPair.getRight();
        COMMON = testSpecPair.getLeft();
    }

    public static Float getFogDensity() {
        return CLIENT.fogDensity.get().floatValue();
    }

    public static float fogColorRed() {
        return CLIENT.fogColorRed.get().floatValue();
    }

    public static float fogColorGreen() {
        return CLIENT.fogColorGreen.get().floatValue();
    }

    public static float fogColorBlue() {
        return CLIENT.fogColorBlue.get().floatValue();
    }

    public static boolean poisonousFog() {
        return COMMON.poisonousFog.get();
    }

    public static int poisonTicks() {
        return COMMON.poisonTicks.get();
    }

    public static int poisonDamage() {
        return COMMON.poisonDamage.get();
    }

}
