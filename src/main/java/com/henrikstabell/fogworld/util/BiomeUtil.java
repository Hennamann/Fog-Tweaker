package com.henrikstabell.fogworld.util;

import com.henrikstabell.fogworld.config.FogWorldConfig;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 07/04/2018.
 * <p>
 * Various utilities for obtaining biome names and ids.
 * Used for the biome blacklist
 */
public class BiomeUtil {

    /**
     * Gets the biome name by {@link Biome#getBiomeName()}
     *
     * @param biome {@link Biome}
     * @return Biome name as a String
     */
    public static String getBiomeName(Biome biome) {
        if (biome != null && biome.getBiomeName() != null) {
            return biome.getBiomeName();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Gets the dimension name using the dimension ID {@link Biome#getBiomeForId(int)}
     *
     * @param biomeID int
     * @return Biome name as a String
     */
    public static String getBiomeName(int biomeID) {
        return getBiomeName(Biome.getBiomeForId(biomeID));
    }

    /**
     * Checks if the biome in question is in the {@link FogWorldConfig#fogBiomeBlacklist}
     *
     * @param biome {@link Biome}
     * @return Blacklisted biome(s)
     */
    public static boolean isBiomeBlacklisted(Biome biome) {
        final List<String> biomeBlacklist = FogWorldConfig.getFogBiomeBlacklist();
        return biomeBlacklist.contains(String.valueOf(Biome.getIdForBiome(biome)))
                || biomeBlacklist.contains(getBiomeName(biome)) || biomeBlacklist.contains(biome.getBiomeName());
    }
}
