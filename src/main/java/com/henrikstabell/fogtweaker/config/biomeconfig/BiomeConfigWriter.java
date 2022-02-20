package com.henrikstabell.fogtweaker.config.biomeconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henrikstabell.fogtweaker.FogTweaker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class BiomeConfigWriter {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Creates a JSON config file for every biome registered in the Biome Registry.
     * {@link net.minecraft.core.Registry#BIOME_REGISTRY}
     * It also creates a few extra files as a utility for players. ex. particle_types.json which contains all registered particles that fog tweaker
     * can use.
     *
     * @param world {@link net.minecraft.world.level.LevelAccessor}
     */
    public static void genBiomeConfigs(LevelAccessor world) {

        Set<ResourceLocation> biomes = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).keySet();

        File directory = new File(BiomeConfig.CONFIG_DIR.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            if (!new File(BiomeConfig.CONFIG_DIR + "/README.txt").exists()) {
                FileWriter readMeWriter = new FileWriter(BiomeConfig.CONFIG_DIR + "/README.txt");
                readMeWriter.write("For more info on editing the configs located here check the wiki: https://github.com/Hennamann/Fog-Tweaker/wiki/Getting-Started-with-the-Config");
                readMeWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (!FogTweaker.BIOME_OVERRIDES.isEmpty()) {
                Writer overriddenBiomesWriter = new FileWriter(BiomeConfig.CONFIG_DIR + "/overridden_biomes.json");
                gson.toJson(FogTweaker.BIOME_OVERRIDES, overriddenBiomesWriter);
                overriddenBiomesWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Writer particleTypesWriter = new FileWriter(BiomeConfig.CONFIG_DIR + "/particle_types.json");
            gson.toJson(ForgeRegistries.PARTICLE_TYPES.getKeys(), particleTypesWriter);
            particleTypesWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ResourceLocation biome : biomes) {
            try {
                File jsonFile = new File(BiomeConfig.CONFIG_DIR + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json");
                if (!jsonFile.exists()) {
                    if (!FogTweaker.BIOME_OVERRIDES.contains(biome)) {
                        jsonFile.getParentFile().mkdirs();
                        Writer biomeConfigWriter = new FileWriter(jsonFile);
                        BiomeFogProperties fogProperties = new BiomeFogProperties(false, 6F, "#FFFFFF", false, "minecraft:ash", 15, false, 1200, 1, biome.getNamespace() + ":" + biome.getPath());
                        gson.toJson(fogProperties, biomeConfigWriter);
                        biomeConfigWriter.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FogTweaker.LOGGER.info("Fog Tweaker: Finished Generating/Updating Biome configs.");
    }

    /**
     * Used for JSON config generation and reading.
     */
    public static class BiomeFogProperties {

        // Fog Related Options
        private final boolean fogEnabled;
        private final float fogDensity;
        private final String fogColor;

        // Fog Particle Related Options
        private final boolean particlesEnabled;
        private final String particleType;
        private final int particleAmount;

        // Poison Related Options
        private final boolean poisonousFogEnabled;
        private final int poisonTicks;
        private final int poisonDamage;

        // Misc
        private final String _comment;

        public BiomeFogProperties(boolean fogEnabled, float fogDensity, String fogColor, boolean particlesEnabled, String particleType, int particleAmount, boolean poisonousFogEnabled, int poisonTicks, int poisonDamage, String _comment) {
            this.fogEnabled = fogEnabled;
            this.fogDensity = fogDensity;
            this.fogColor = fogColor;
            this.particlesEnabled = particlesEnabled;
            this.particleType = particleType;
            this.particleAmount = particleAmount;
            this.poisonousFogEnabled = poisonousFogEnabled;
            this.poisonTicks = poisonTicks;
            this.poisonDamage = poisonDamage;
            this._comment = _comment;
        }

        public boolean isFogEnabled() {
            return fogEnabled;
        }

        public float getFogDensity() {
            return fogDensity;
        }

        public String getFogColor() {
            return fogColor;
        }

        public boolean isPoisonousFogEnabled() {
            return poisonousFogEnabled;
        }

        public int getPoisonTicks() {
            return poisonTicks;
        }

        public int getPoisonDamage() {
            return poisonDamage;
        }

        public boolean isParticlesEnabled() {
            return particlesEnabled;
        }

        public String getParticleType() {
            return particleType;
        }

        public int getParticleAmount() {
            return particleAmount;
        }
    }
}
