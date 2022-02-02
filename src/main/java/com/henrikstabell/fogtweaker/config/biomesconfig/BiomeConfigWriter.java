package com.henrikstabell.fogtweaker.config.biomesconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henrikstabell.fogtweaker.FogTweaker;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class BiomeConfigWriter {

    private static final File configDir = new File(Minecraft.getInstance().gameDirectory + "/config/fogtweaker/biomes");

    /**
     * Creates a JSON config file for every biome registered in the Forge Biome Registry.
     * {@link net.minecraftforge.registries.ForgeRegistries#BIOMES}
     * It also creates a few extra files as a utility for players. ex. particle_types.json which contains all registered particles that fog tweaker
     * can use.
     */
    public static void genBiomeConfigs() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Set<ResourceLocation> biomes = ForgeRegistries.BIOMES.getKeys();

        File directory = new File(configDir.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            if (!new File(configDir + "/README.txt").exists()) {
                FileWriter readmeWriter = new FileWriter(configDir + "/README.txt");
                readmeWriter.write("For more info on editing the configs located here check the wiki: https://github.com/Hennamann/Fog-Tweaker/wiki/Getting-Started-with-the-Config");
                readmeWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (!FogTweaker.biomeOverrides.isEmpty()) {
                Writer writer = new FileWriter(configDir + "/overridden_biomes.json");
                gson.toJson(FogTweaker.biomeOverrides, writer);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Writer writer = new FileWriter(configDir + "/particle_types.json");
            gson.toJson(ForgeRegistries.PARTICLE_TYPES.getKeys(), writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ResourceLocation biome : biomes) {
            File namespaceDir = new File(configDir + "/" + biome.getNamespace());
            try {
                if (!namespaceDir.exists()) {
                    namespaceDir.mkdir();
                }
                File jsonFile = new File(configDir + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json");
                if (!jsonFile.exists()) {
                    if (!FogTweaker.biomeOverrides.contains(biome)) {
                        Writer writer = new FileWriter(configDir + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json");
                        BiomeFogProperties fogProperties = new BiomeFogProperties(false, 6F, "#FFFFFF", false, "minecraft:ash", 15, false, 1200, 1, biome.getNamespace() + ":" + biome.getPath());
                        gson.toJson(fogProperties, writer);
                        writer.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        FogTweaker.LOGGER.info("Fog Tweaker: Finished Generating/Updating Biome configs.");
    }
}
