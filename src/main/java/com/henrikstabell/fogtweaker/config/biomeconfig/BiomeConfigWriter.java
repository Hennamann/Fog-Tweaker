package com.henrikstabell.fogtweaker.config.biomeconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henrikstabell.fogtweaker.FogTweaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class BiomeConfigWriter {

    /**
     * Creates a JSON config file for every biome registered in the Forge Biome Registry.
     * {@link net.minecraftforge.registries.ForgeRegistries#BIOMES}
     * It also creates a few extra files as a utility for players. ex. particle_types.json which contains all registered particles that fog tweaker
     * can use.
     */
    public static void genBiomeConfigs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Set<ResourceLocation> biomes = ForgeRegistries.BIOMES.getKeys();

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
            if (!FogTweaker.biomeOverrides.isEmpty()) {
                Writer overiddenBiomesWriter = new FileWriter(BiomeConfig.CONFIG_DIR + "/overridden_biomes.json");
                gson.toJson(FogTweaker.biomeOverrides, overiddenBiomesWriter);
                overiddenBiomesWriter.close();
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
            File namespaceDir = new File(BiomeConfig.CONFIG_DIR + "/" + biome.getNamespace());
            try {
                if (!namespaceDir.exists()) {
                    namespaceDir.mkdir();
                }
                File jsonFile = new File(BiomeConfig.CONFIG_DIR + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json");
                if (!jsonFile.exists()) {
                    if (!FogTweaker.biomeOverrides.contains(biome)) {
                        Writer biomeConfigWriter = new FileWriter(BiomeConfig.CONFIG_DIR + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json");
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
}
