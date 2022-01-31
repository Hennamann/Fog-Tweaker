package com.henrikstabell.fogworld.handler;

import com.henrikstabell.fogworld.FogWorld;
import com.henrikstabell.fogworld.config.Configuration;
import com.henrikstabell.fogworld.config.biomesconfig.BiomeConfigReader;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber(modid = FogWorld.MODID, value = Dist.CLIENT)
public class FogEventHandler {

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event) {
        Level world = event.getCamera().getEntity().level;
        BlockPos pos = event.getCamera().getBlockPosition();
        ResourceLocation biome = world.getBiome(pos).getRegistryName();

        if (!FogWorld.biomeOverrides.contains(biome)) {
            if (Configuration.getFogEnabled()) {
                assert biome != null;
                if (BiomeConfigReader.doesBiomeConfigExist(biome)) {
                    boolean fogEnabled = BiomeConfigReader.readBiomeConfig(biome).isFogEnabled();
                    if (fogEnabled) {
                        String fogColorString = BiomeConfigReader.readBiomeConfig(biome).getFogColor();
                        Color fogColor = Color.decode(fogColorString);

                        float red = fogColor.getRed();
                        float green = fogColor.getGreen();
                        float blue = fogColor.getBlue();

                        final float[] fogColors = {red / 255F, green / 255F, blue / 255F};

                        event.setRed(fogColors[0]);
                        event.setGreen(fogColors[1]);
                        event.setBlue(fogColors[2]);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Level world = event.getCamera().getEntity().level;
        BlockPos pos = event.getCamera().getBlockPosition();
        ResourceLocation biome = world.getBiome(pos).getRegistryName();

        if (Configuration.getFogEnabled()) {
            if (!FogWorld.biomeOverrides.contains(biome)) {
                assert biome != null;
                if (BiomeConfigReader.doesBiomeConfigExist(biome)) {
                    boolean fogEnabled = BiomeConfigReader.readBiomeConfig(biome).isFogEnabled();
                    float configFogDensity = BiomeConfigReader.readBiomeConfig(biome).getFogDensity();
                    if (fogEnabled) {
                        float fogDensity = configFogDensity;
                        float fogDensityModifier = 1F;
                        fogDensityModifier += (float) (0.1F * event.getPartialTicks());;
                        fogDensityModifier = Mth.clampedLerp(fogDensityModifier, 0F, 1F);
                        fogDensity = fogDensity >= event.getFarPlaneDistance() ? event.getFarPlaneDistance() : Mth.clampedLerp(fogDensity, event.getFarPlaneDistance(), fogDensityModifier);

                        if (event.getMode() == FogRenderer.FogMode.FOG_SKY) {
                            RenderSystem.setShaderFogStart(0.0F);
                            RenderSystem.setShaderFogEnd(fogDensity);
                        } else {
                            RenderSystem.setShaderFogStart(fogDensity * 0.75F);
                            RenderSystem.setShaderFogEnd(fogDensity);
                        }
                    }
                }
            }
        }
    }
}