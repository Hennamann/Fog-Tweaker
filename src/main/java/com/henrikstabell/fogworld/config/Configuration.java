package com.henrikstabell.fogworld.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
public class Configuration {

    public static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
    }

    public static boolean getFogEnabled() {
        return CLIENT.fogEnabled.get();
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

    public static boolean incompatibleModsWarning() {
        return CLIENT.incompatibleModsWarning.get();
    }

    public static int poisonTicks() {
        return COMMON.poisonTicks.get();
    }

    public static int poisonDamage() {
        return COMMON.poisonDamage.get();
    }

    public static class Client {

        public final BooleanValue fogEnabled;
        public final IntValue fogDensity;
        public final IntValue fogColorRed;
        public final IntValue fogColorGreen;
        public final IntValue fogColorBlue;
        public final BooleanValue incompatibleModsWarning;

        public Client(ForgeConfigSpec.Builder builder) {
            fogEnabled = builder
                    .comment("Disables/Enables the fog rendering introduced by fog world. Vanilla fog is not disabled with this option")
                    .translation("config.client.fogworld.fogenabled")
                    .define("fogEnabled", true);
            fogDensity = builder
                    .comment("Controls the amount/density of the fog.")
                    .translation("config.client.fogworld.fogdensity")
                    .defineInRange("fogDensity", 3, 1, 600);
            fogColorRed = builder
                    .comment("Set the RED color value for the fog. Minimum 0, maximum 255")
                    .translation("config.client.fogworld.fogcolorred")
                    .defineInRange("fogColorRed", 0, 0, 255);
            fogColorGreen = builder
                    .comment("Set the GREEN color value for the fog. Minimum 0, maximum 255")
                    .translation("config.client.fogworld.fogcolorgreen")
                    .defineInRange("fogColorGreen", 0, 0, 255);
            fogColorBlue = builder
                    .comment("Set the BLUE color value for the fog. Minimum 0, maximum 255")
                    .translation("config.client.fogworld.fogcolorblue")
                    .defineInRange("fogColorBlue", 0, 0, 255);
            incompatibleModsWarning = builder
                    .comment("Enable warnings on load when using incompatible mods")
                    .translation("config.client.fogworld.incompatiblemodswarning")
                    .define("incompatibleModsWarning", true);
        }
    }

    public static class Common {

        public final IntValue poisonTicks;
        public final IntValue poisonDamage;
        public final BooleanValue poisonousFog;

        public Common(ForgeConfigSpec.Builder builder) {
            poisonousFog = builder
                    .comment("Should the fog be poisonous?")
                    .translation("config.common.fogworld.poisonousFog")
                    .define("poisonousFog", false);
            poisonTicks = builder
                    .comment("How many ticks before the player takes damage in the fog? Minimum 20 (1 second), maximum 72000 (1 hour)")
                    .translation("config.common.fogworld.poisonticks")
                    .defineInRange("poisonTicks", 1200, 20, 72000);
            poisonDamage = builder
                    .comment("How much damage should the poison fog deal per tick? Minimum 1, Maximum 20")
                    .translation("config.common.fogworld.poisondamage")
                    .defineInRange("poisonDamage", 1, 1, 20);
        }
    }

}
