package com.henrikstabell.fogtweaker.config.biomeconfig;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BiomeConfigReader {

    /**
     * Reads the JSON config files that have been generated by {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfigWriter}.
     * Used to get Fog, Particle and Poison settings for each biome.
     *
     * This should only be used by {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig} during load and config changes
     * to avoid performance issues.
     *
     * @param biomeKey {@link net.minecraft.resources.ResourceLocation}
     * @return a new instance of {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfigWriter.BiomeFogProperties} created from the relevant
     * JSON file.
     */
    protected static BiomeConfigWriter.BiomeFogProperties readBiomeConfig(ResourceLocation biomeKey) {

        BiomeConfigWriter.BiomeFogProperties fogProperties;

        if (biomeKey == null) {
            return new BiomeConfigWriter.BiomeFogProperties(false, 6F, "#FFFFFF", false, "minecraft:ash", 15, false, 1200, 1, biomeKey.getNamespace() + ":" + biomeKey.getPath());
        }
        try {
            Gson gson = new Gson();

            File allBiomesFile = new File(BiomeConfig.CONFIG_DIR + "/" + "all_biomes.json"); // Override to set the same settings for every biome.
            if (allBiomesFile.exists()) {
                Reader allBiomesFileReader = Files.newBufferedReader(Paths.get(BiomeConfig.CONFIG_DIR + "/" + "all_biomes.json"));
                fogProperties = gson.fromJson(allBiomesFileReader, BiomeConfigWriter.BiomeFogProperties.class);
                allBiomesFileReader.close();
            } else {
                Reader biomeConfigReader = Files.newBufferedReader(Paths.get(BiomeConfig.CONFIG_DIR + "/" + biomeKey.getNamespace() + "/" + biomeKey.getPath() + ".json"));
                fogProperties = gson.fromJson(biomeConfigReader, BiomeConfigWriter.BiomeFogProperties.class);
                biomeConfigReader.close();
            }
            return fogProperties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BiomeConfigWriter.BiomeFogProperties(false, 6F, "#FFFFFF", false, "minecraft:ash", 15, false, 1200, 1, biomeKey.getNamespace() + ":" + biomeKey.getPath());
    }

    /**
     * Checks whether a config file exists for the specified Biome and returns a value of true if it exists, false if it does not exist.
     * Used to prevent reading values from a null config file.
     *
     * This should only be used by {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig} during load and config changes
     * to avoid performance issues.
     *
     * @param biomeKey {@link net.minecraft.resources.ResourceLocation}
     * @return boolean
     */
    protected static boolean doesBiomeConfigExist(ResourceLocation biomeKey) {
        return new File(BiomeConfig.CONFIG_DIR + "/" + biomeKey.getNamespace() + "/" + biomeKey.getPath() + ".json").exists();
    }
}
