package com.henrikstabell.fogworld.config.biomesconfig;

/**
 * Used for JSON config generation and reading.
 */
public class BiomeFogProperties {

    // Fog Related Options
    private boolean fogEnabled;
    private float fogDensity;
    private String fogColor;

    // Poison Related Options
    private boolean poisonousFogEnabled;
    private int poisonTicks;
    private int poisonDamage;

    // Purely for utility… Not used in actual code.
    private String _comment;

    public BiomeFogProperties() {}

    public BiomeFogProperties(boolean fogEnabled, float fogDensity, String fogColor, boolean poisonousFogEnabled, int poisonTicks, int poisonDamage, String _comment) {
        this.fogEnabled = fogEnabled;
        this.fogDensity = fogDensity;
        this.fogColor = fogColor;
        this.poisonousFogEnabled = poisonousFogEnabled;
        this.poisonTicks = poisonTicks;
        this.poisonDamage = poisonDamage;
        this._comment = _comment;
    }

    public boolean isFogEnabled() {
        return fogEnabled;
    }

    public float getFogDensity() {
        return fogDensity;
    }

    public String getFogColor() {
        return fogColor;
    }

    public boolean isPoisonousFogEnabled() {
        return poisonousFogEnabled;
    }

    public int getPoisonTicks() {
        return poisonTicks;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }
}
