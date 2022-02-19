package com.henrikstabell.fogtweaker.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

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

    public static boolean getFogParticlesEnabled() {
        return CLIENT.fogParticlesEnabled.get();
    }

    public static boolean getIncompatibleModsWarningEnabled() {
        return CLIENT.incompatibleModsWarningEnabled.get();
    }

    public static boolean getPoisonousFogEnabled() {
        return COMMON.poisonousFogEnabled.get();
    }

    public static class Client {

        public final BooleanValue fogEnabled;
        public final BooleanValue fogParticlesEnabled;
        public final BooleanValue incompatibleModsWarningEnabled;

        public Client(ForgeConfigSpec.Builder builder) {
            fogEnabled = builder
                    .comment("Disables/Enables the fog rendering introduced by Fog Tweaker. Vanilla fog is not disabled with this option. NOTE: This will disregard individual Biome Configs!")
                    .translation("config.client.fogtweaker.fogenabled")
                    .define("fogEnabled", true);
            fogParticlesEnabled = builder
                    .comment("Disables/Enables configurable particles spawning in the fog. NOTE: This will disregard individual Biome Configs!")
                    .translation("config.client.fogtweaker.fogparticlesenabled")
                    .define("fogParticlesEnabled", true);
            incompatibleModsWarningEnabled = builder
                    .comment("Enable warnings on load when using incompatible mods.")
                    .translation("config.client.fogtweaker.incompatiblemodswarning")
                    .define("incompatibleModsWarning", true);
        }
    }

    public static class Common {

        public final BooleanValue poisonousFogEnabled;

        public Common(ForgeConfigSpec.Builder builder) {
            poisonousFogEnabled = builder
                    .comment("Should the fog be poisonous? NOTE: This will disregard individual Biome Configs!")
                    .translation("config.common.fogtweaker.poisonousFog")
                    .define("poisonousFogEnabled", true);
        }
    }
}
