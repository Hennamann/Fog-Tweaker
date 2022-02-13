package com.henrikstabell.fogtweaker.config.biomeconfig;

/**
 * Used for JSON config generation and reading.
 */
public class BiomeFogProperties {

    // Fog Related Options
    private final boolean fogEnabled;
    private final float fogDensity;
    private final String fogColor;

    // Fog Particle Related Options
    private final boolean particlesEnabled;
    private final String particleType;
    private final int particleAmount;

    // Poison Related Options
    private final boolean poisonousFogEnabled;
    private final int poisonTicks;
    private final int poisonDamage;

    public BiomeFogProperties(boolean fogEnabled, float fogDensity, String fogColor, boolean particlesEnabled, String particleType, int particleAmount, boolean poisonousFogEnabled, int poisonTicks, int poisonDamage, String _comment) {
        this.fogEnabled = fogEnabled;
        this.fogDensity = fogDensity;
        this.fogColor = fogColor;
        this.particlesEnabled = particlesEnabled;
        this.particleType = particleType;
        this.particleAmount = particleAmount;
        this.poisonousFogEnabled = poisonousFogEnabled;
        this.poisonTicks = poisonTicks;
        this.poisonDamage = poisonDamage;
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

    public boolean isParticlesEnabled() {
        return particlesEnabled;
    }

    public String getParticleType() {
        return particleType;
    }

    public int getParticleAmount() {
        return particleAmount;
    }
}
