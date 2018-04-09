package com.henrikstabell.fogworld.util;

import com.henrikstabell.fogworld.config.FogWorldConfig;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 07/04/2018.
 * <p>
 * Various utilities for obtaining dimension names and ids.
 * Used for the dimension blacklist
 */
public class DimensionUtil {

    /**
     * Gets the dimension name by {@link DimensionType#getName()}
     *
     * @param dimension {@link DimensionType}
     * @return Dimension name as a String
     */
    public static String getDimensionName(DimensionType dimension) {
        if (dimension != null && dimension.getName() != null) {
            return dimension.getName();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Gets the dimension name using the id of the dimension
     *
     * @param dimensionID int
     * @return Dimension Name as a String
     */
    public static String getDimensionName(int dimensionID) {
        return getDimensionName(DimensionManager.getProviderType(dimensionID));
    }

    /**
     * Checks if the dimension in question is in the {@link FogWorldConfig#fogDimensionBlacklist}
     *
     * @param dimension {@link DimensionType}
     * @return Blacklisted dimension(s)
     */
    public static boolean isDimensionBlacklisted(DimensionType dimension) {
        final List<String> dimensionBlacklist = FogWorldConfig.getFogDimensionBlacklist();
        return dimensionBlacklist.contains(String.valueOf(dimension.getId()))
                || dimensionBlacklist.contains(getDimensionName(dimension)) || dimensionBlacklist.contains(dimension.getName());
    }
}
