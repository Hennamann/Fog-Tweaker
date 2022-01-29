package com.henrikstabell.fogworld.config.biomesconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henrikstabell.fogworld.FogWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class BiomeConfigGenerator {

    private static final File configDir = new File(Minecraft.getInstance().gameDirectory + "/config/fogworld/biomes");

    /**
     * Creates a JSON config file for every biome registered in the Forge Biome Registry.
     * {@link net.minecraftforge.registries.ForgeRegistries#BIOMES}
     * TODO: VERY ALPHA! SUBJECT TO CHANGE!
     */
    public static void genBiomeConfigs() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Set<ResourceLocation> biomes = ForgeRegistries.BIOMES.getKeys();

        File directory = new File(configDir.toString());
        if (!directory.exists()){
            directory.mkdirs();
        }
        try {
            if (!new File(configDir + "/README.txt").exists()) {
                FileWriter readmeWriter = new FileWriter(configDir + "/README.txt");
                readmeWriter.write("For more info on editing the configs located here check the wiki: https://github.com/Hennamann/Fog-World/wiki/Getting-Started-with-the-Config");
                readmeWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ResourceLocation biome : biomes) {
            File namespaceDir = new File (configDir + "/" + biome.getNamespace());
            try {
                if (!namespaceDir.exists()) {
                    namespaceDir.mkdir();
                }
                File jsonFile = new File(configDir + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json");
                if (!jsonFile.exists()) {
                    Writer writer = new FileWriter(configDir + "/" + biome.getNamespace() + "/" + biome.getPath() + ".json");
                    BiomeFogProperties fogProperties = new BiomeFogProperties( false, 6F, 0F, 0F, 0F, false, 1200, 1, biome.getNamespace() + ":" + biome.getPath());
                    gson.toJson(fogProperties, writer);
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FogWorld.LOGGER.info("Fog World: Finished Generating/Updating Biome configs.");
    }
}
