package com.henrikstabell.fogworld.util;

import com.henrikstabell.fogworld.core.FogConfig;
import net.minecraft.world.DimensionType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 07/04/2018.
 */
public class DimensionUtil {

    public static String getDimensionName(DimensionType dimension) {
        if (dimension != null && dimension.getName() != null) {
            return dimension.getName();
        }
        return StringUtils.EMPTY;
    }

    public static String getDimensionName(int dimensionID) {
        return getDimensionName(DimensionType.getById(dimensionID));
    }

    public static boolean dimensionIsBlacklisted(DimensionType dimension) {
        final List<String> dimensionBlacklist = FogConfig.getFogDimenstionBlacklist();
        return dimensionBlacklist.contains(String.valueOf(DimensionType.getById(dimension.getId())))
                || dimensionBlacklist.contains(getDimensionName(dimension)) || dimensionBlacklist.contains(dimension.getName());
    }
}
