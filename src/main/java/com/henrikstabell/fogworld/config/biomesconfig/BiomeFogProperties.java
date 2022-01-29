package com.henrikstabell.fogworld.config.biomesconfig;

/**
 * Used for JSON config generation and reading.
 */
public class BiomeFogProperties {

    // Fog Related Options
    private boolean fogEnabled;
    private float fogDensity;
    private float fogColorRed;
    private float fogColorGreen;
    private float fogColorBlue;

    // Poison Related Options
    private boolean poisonousFogEnabled;
    private int poisonTicks;
    private int poisonDamage;

    // Purely for utility… Not used in actual code.
    private String _comment;

    public BiomeFogProperties() {

    }

    public BiomeFogProperties(boolean fogEnabled, float fogDensity, float fogColorRed, float fogColorGreen, float fogColorBlue, boolean poisonousFogEnabled, int poisonTicks, int poisonDamage, String _comment) {
        this.fogEnabled = fogEnabled;
        this.fogDensity = fogDensity;
        this.fogColorRed = fogColorRed;
        this.fogColorGreen = fogColorGreen;
        this.fogColorBlue = fogColorBlue;
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

    public float getFogColorRed() {
        return fogColorRed;
    }

    public float getFogColorGreen() {
        return fogColorGreen;
    }

    public float getFogColorBlue() {
        return fogColorBlue;
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
