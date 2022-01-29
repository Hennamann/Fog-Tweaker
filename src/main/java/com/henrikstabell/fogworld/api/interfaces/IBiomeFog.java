package com.henrikstabell.fogworld.api.interfaces;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 09/04/2018.
 * <p>
 * Implement this on biomes where you want to override the default fog settings applied by Fog World
 *
 * This is likely to be removed post-1.18 alpha. Use the JSON config system instead!
 */
@Deprecated
public interface IBiomeFog {

    /**
     * Should the fog be enabled or disabled in this biome.
     *
     * @return false to disable the fog
     *
     * This is likely to be removed post-1.18 alpha. Use the JSON config system instead!
     */
    @Deprecated
    default boolean getFogEnabled() {
        return true;
    }

    /**
     * Set the density/how much fog there should be in the biome.
     *
     * @return 0 to have no fog at all.
     *
     * This is likely to be removed post-1.18 alpha. Use the JSON config system instead!
     */
    @Deprecated
    float getFogDensity();

    /**
     * Sets the red colorvalue used to mix the fogcolor.
     *
     * @return 0 for no red colorvalue.
     *
     * This is likely to be removed post-1.18 alpha. Use the JSON config system instead!
     */
    @Deprecated
    int getRedFogColor();

    /**
     * Sets the green colorvalue used to mix the fogcolor.
     *
     * @return 0 for no green colorvalue.
     *
     * This is likely to be removed post-1.18 alpha. Use the JSON config system instead!
     */
    @Deprecated
    int getGreenFogColor();

    /**
     * Sets the blue colorvalue used to mix the fogcolor.
     *
     * @return for 0 no blue colorvalue.
     *
     * This is likely to be removed post-1.18 alpha. Use the JSON config system instead!
     */
    @Deprecated
    int getBlueFogColor();
}