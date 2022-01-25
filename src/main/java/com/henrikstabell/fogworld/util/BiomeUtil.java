package com.henrikstabell.fogworld.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 07/04/2018.
 * <p>
 * Various utility methods for obtaining biome names and ids.
 * Used for the biome whitelist
 */
public class BiomeUtil {

    /**
     * Gets the biome name/resourcekey by {@link net.minecraft.world.level.Level#getBiomeName(BlockPos)} ()}
     *
     * @param biomeKey {@link ResourceKey<Biome>}
     * @return Biome ResourceKey as {@link Optional<ResourceKey>}
     */
    public static Optional<ResourceKey<Biome>> getBiome(ResourceKey<Biome> biomeKey) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            if (biomeKey != null) {
                return Minecraft.getInstance().level.getBiomeName(Minecraft.getInstance().player.blockPosition());
            }
        }
        return null;
    }
}
