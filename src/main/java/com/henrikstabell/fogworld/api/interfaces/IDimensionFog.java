package com.henrikstabell.fogworld.api.interfaces;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 09/04/2018.
 * <p>
 * Implement this on dimensions where you want to override the default fog settings applied by Fog World
 */
public interface IDimensionFog {

    /**
     * Should the fog be enabled or disabled in this dimension.
     *
     * @return false to disable the fog
     */
    default boolean getFogEnabled() {
        return true;
    }

    /**
     * Set the density/how much fog there should be in this dimension.
     *
     * @return 0 to have no fog at all.
     */
    float getFogDensity();

    /**
     * Sets the red colorvalue used to mix the fogcolor.
     *
     * @return 0 for no red colorvalue.
     */
    int getRedFogColor();

    /**
     * Sets the green colorvalue used to mix the fogcolor.
     *
     * @return 0 for no green colorvalue.
     */
    int getGreenFogColor();

    /**
     * Sets the blue colorvalue used to mix the fogcolor.
     *
     * @return for 0 no blue colorvalue.
     */
    int getBlueFogColor();
}
