package com.henrikstabell.fogworld.api.interfaces;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 09/04/2018.
 * <p>
 * Implement this on biomes where you want to override the default fog settings applied by Fog World
 */
public interface IBiomeFog {

    /**
     * Should the fog be enabled or disabled in this biome.
     *
     * @return boolean
     */
    default boolean getFogEnabled() {
        return true;
    }

    /**
     * Set the density/how much mist there should be in the biome.
     *
     * @return 0 to have no fog at all.
     */
    float getFogDensity(int var1, int var2, int var3);

    /**
     * Sets the color of the fog. Uses decimal values for color.
     *
     * @return 0 for black fog.
     */
    int getFogColor(int var1, int var2, int var3);
}
