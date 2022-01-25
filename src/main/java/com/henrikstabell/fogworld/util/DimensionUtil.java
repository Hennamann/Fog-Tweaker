package com.henrikstabell.fogworld.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 07/04/2018.
 * <p>
 * Various utility methods for obtaining dimension names and ids.
 * Used for the dimension blacklist
 */
public class DimensionUtil {

    /**
     * Gets the dimension name by {@link Level#dimension()}
     *
     * @param dimensionKey {@link ResourceKey<Level>}
     * @return Dimension ResourceKey as {@link ResourceKey<Level>}
     */
    public static ResourceKey<Level> getDimensionName(ResourceKey<Level> dimensionKey) {
        if (Minecraft.getInstance().level != null) {
            if (dimensionKey != null) {
                return Minecraft.getInstance().level.dimension();
            }
        }
        return null;
    }
}