package com.henrikstabell.fogtweaker.config.biomeconfig;

import com.electronwill.nightconfig.core.file.FileWatcher;
import com.henrikstabell.fogtweaker.FogTweaker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class BiomeConfig {

    public static final File CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get() + "/fogtweaker/biomes");

    private static final Map<ResourceLocation, BiomeConfigWriter.BiomeFogProperties> BIOME_CONFIGS = new HashMap<>();

    /**
     * Gets all the generated JSON Biome config files and loads them into a HashMap {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig#BIOME_CONFIGS}
     * for use in a static context. Also sets up File Watchers using {@link com.electronwill.nightconfig.core.file.FileWatcher}
     * to update the configs whenever a JSON config file is changed.
     *
     * @param world {@link net.minecraft.world.level.LevelAccessor}
     */
    public static void readBiomeConfigs(LevelAccessor world) {
        Set<ResourceLocation> biomes = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).keySet();

        for (ResourceLocation biome : biomes) {
            if (BiomeConfigReader.doesBiomeConfigExist(biome)) {
                BIOME_CONFIGS.putIfAbsent(biome, BiomeConfigReader.readBiomeConfig(biome));
                try {
                    FileWatcher.defaultInstance().addWatch(Paths.get(CONFIG_DIR + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json"), new BiomeConfigWatcher(biome));
                } catch (IOException e) {
                    throw new RuntimeException("Fog Tweaker: Failed to watch Biome Config File", e);
                }
            }
        }
    }

    /**
     * Replaces the given Biome in the {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig#BIOME_CONFIGS} map.
     * Used to update the config when the JSON config files are changed.
     *
     * This function is only intended for the config system, hence why it is protected, it is not intended for
     * mod developers! An API for mod developers, allowing "recommended" fog settings for a specific biome to be set is planned.
     *
      * @param biomeKey {@link net.minecraft.resources.ResourceLocation}
     */
    protected static void updateBiomeConfigFor(ResourceLocation biomeKey) {
        BIOME_CONFIGS.replace(biomeKey, BiomeConfigReader.readBiomeConfig(biomeKey));
        FogTweaker.LOGGER.info("Fog Tweaker: Successfully Updated Biome Config for " + biomeKey + "." );
    }

    /**
     * Used to get the Biome Config for the given biome from the {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig#BIOME_CONFIGS} map.
     *
     * @param biomeKey {@link net.minecraft.resources.ResourceLocation}
     * @return {@link com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfigWriter.BiomeFogProperties}
     */
    public static BiomeConfigWriter.BiomeFogProperties getBiomeConfigFor(ResourceLocation biomeKey) {
        return BIOME_CONFIGS.get(biomeKey);
    }
}
