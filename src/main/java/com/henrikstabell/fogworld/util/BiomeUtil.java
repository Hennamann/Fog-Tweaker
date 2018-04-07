package com.henrikstabell.fogworld.util;

import com.henrikstabell.fogworld.core.FogConfig;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 07/04/2018.
 */
public class BiomeUtil {

    public static String getBiomeName(Biome biome) {
        if (biome != null && biome.getBiomeName() != null) {
            return biome.getBiomeName();
        }
        return StringUtils.EMPTY;
    }

    public static String getBiomeName(int biomeID) {
        return getBiomeName(Biome.getBiomeForId(biomeID));
    }

    public static boolean biomeIsBlacklisted(Biome biome) {
        final List<String> biomeBlacklist = FogConfig.getFogBiomeBlacklist();
        return biomeBlacklist.contains(String.valueOf(Biome.getIdForBiome(biome)))
                || biomeBlacklist.contains(getBiomeName(biome)) || biomeBlacklist.contains(biome.getBiomeName());
    }
}
