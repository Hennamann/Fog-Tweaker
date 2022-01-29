package com.henrikstabell.fogworld.config.biomesconfig;

/**
 * Used for JSON config generation.
 */
public class BiomeFogProperties {

    // Fog Related Options
    private boolean fogEnabled;
    private float fogDensity;

    // Poison Related Options
    private boolean poisonousFogEnabled;
    private int poisonTicks;
    private int poisonDamage;

    private String _comment;

    public BiomeFogProperties() {

    }

    public BiomeFogProperties(boolean fogEnabled, float fogDensity, boolean poisonousFogEnabled, int poisonTicks, int poisonDamage, String _comment) {
        this.fogEnabled = fogEnabled;
        this.fogDensity = fogDensity;
        this.poisonousFogEnabled = poisonousFogEnabled;
        this.poisonTicks = poisonTicks;
        this.poisonDamage = poisonDamage;
        this._comment = _comment;
    }

}
