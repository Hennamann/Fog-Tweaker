package com.henrikstabell.fogtweaker.config.biomeconfig;

import com.henrikstabell.fogtweaker.FogTweaker;
import net.minecraft.resources.ResourceLocation;

public class BiomeConfigWatcher implements Runnable {

    private final ResourceLocation biome;

    public BiomeConfigWatcher(ResourceLocation biome) {
        this.biome = biome;
    }

    @Override
    public void run() {
        FogTweaker.LOGGER.info("Fog Tweaker: Biome Config File for " + biome.toString() + " changed, updating configs…");
        BiomeConfig.updateBiomeConfigFor(biome);
    }
}
