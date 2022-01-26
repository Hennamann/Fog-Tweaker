package com.henrikstabell.fogworld.handler;

import com.henrikstabell.fogworld.FogWorld;
import com.henrikstabell.fogworld.config.Configuration;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FogWorld.MODID, value = Dist.CLIENT)
public class FogEventHandler {

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event) {
        if (Configuration.getFogEnabled()) {
            float red = Configuration.fogColorRed();
            float green = Configuration.fogColorGreen();
            float blue = Configuration.fogColorBlue();

            final float[] fogColors = {Mth.cos(red), Mth.cos(green), Mth.cos(blue)};

            event.setRed(fogColors[0]);
            event.setGreen(fogColors[1]);
            event.setBlue(fogColors[2]);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (Configuration.getFogEnabled()) {
            float fogDensity = Configuration.getFogDensity();
            float fogDensityModifier = 1F;
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