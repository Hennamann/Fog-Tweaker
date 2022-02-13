package com.henrikstabell.fogtweaker.config.biomeconfig;

import com.electronwill.nightconfig.core.file.FileWatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class BiomeConfig {

    private static final Map<ResourceLocation, BiomeFogProperties> BIOME_CONFIGS = new HashMap<>();
    private static final File configDir = new File(Minecraft.getInstance().gameDirectory + "/config/fogtweaker/biomes");

    public static void readBiomeConfigs() {
        Set<ResourceLocation> biomes = ForgeRegistries.BIOMES.getKeys();

        for (ResourceLocation biome : biomes) {
            if (BiomeConfigReader.doesBiomeConfigExist(biome)) {
                BIOME_CONFIGS.putIfAbsent(biome, BiomeConfigReader.readBiomeConfig(biome));
                try {
                    FileWatcher.defaultInstance().addWatch(Paths.get(configDir + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json"), new BiomeConfigWatcher(biome));
                } catch (IOException e) {
                    throw new RuntimeException("Failes to watch Biome config file", e);
                }
            }
        }
    }

    protected static void updateBiomeConfigFor(ResourceLocation biome) {
        BIOME_CONFIGS.replace(biome, BiomeConfigReader.readBiomeConfig(biome));
    }

    public static BiomeFogProperties getBiomeConfigFor(ResourceLocation biome) {
        return BIOME_CONFIGS.get(biome);
    }
}
