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

@Mod.EventBusSubscriber(modid = FogWorld.MODID, value = Dist.CLIENT)
public class FogEventHandler {

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event) {
        Level world = event.getCamera().getEntity().level;
        BlockPos pos = event.getCamera().getBlockPosition();
        ResourceLocation biome = world.getBiome(pos).getRegistryName();

        if (!FogWorld.biomeOverrides.contains(biome)) {
            if (Configuration.getFogEnabled()) {
                if (BiomeConfigReader.doesBiomeConfigExist(biome)) {
                    boolean fogEnabled = BiomeConfigReader.readBiomeConfig(biome).isFogEnabled();
                    if (fogEnabled) {
                        float red = BiomeConfigReader.readBiomeConfig(biome).getFogColorRed();
                        float green = BiomeConfigReader.readBiomeConfig(biome).getFogColorGreen();
                        float blue = BiomeConfigReader.readBiomeConfig(biome).getFogColorBlue();

                        if (red >= 254F) {
                            red = 254F;
                        }
                        if (green >= 254F) {
                            green = 254F;
                        }
                        if (blue >= 254F) {
                            blue = 254F;
                        }

                        final float[] fogColors = {Mth.cos(red), Mth.cos(green), Mth.cos(blue)}; //Seems to fix weird color blending issues… ¯\_(ツ)_/¯

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
                if (BiomeConfigReader.doesBiomeConfigExist(biome)) {
                    boolean fogEnabled = BiomeConfigReader.readBiomeConfig(biome).isFogEnabled();
                    float configFogDensity = BiomeConfigReader.readBiomeConfig(biome).getFogDensity();
                    if (fogEnabled) {
                        float fogDensity = configFogDensity;
                        float fogDensityModifier = 0.5F;
                        float fogShift = (float) (0.001F * event.getPartialTicks());
                        fogDensityModifier -= fogShift;
                        fogDensityModifier = Mth.clamp(fogDensityModifier, 0F, 1F);
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