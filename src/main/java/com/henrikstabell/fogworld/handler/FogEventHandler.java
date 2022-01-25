package com.henrikstabell.fogworld.handler;

import com.henrikstabell.fogworld.FogWorld;
import com.henrikstabell.fogworld.config.FogWorldConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FogWorld.MODID, value = Dist.CLIENT)
public class FogEventHandler {

    private static float fogDensityModifier = 1F;

    @SubscribeEvent
    public static void fogColours(EntityViewRenderEvent.FogColors event) {
        float red = FogWorldConfig.fogColorRed();
        float green = FogWorldConfig.fogColorGreen();
        float blue = FogWorldConfig.fogColorBlue();

        final float[] fogColors = {Mth.cos(red), Mth.cos(green), Mth.cos(blue)};

        event.setRed(fogColors[0]);
        event.setGreen(fogColors[1]);
        event.setBlue(fogColors[2]);
    }

    @SubscribeEvent
    public static void fog(EntityViewRenderEvent.RenderFogEvent event) {
        float fogDensity = FogWorldConfig.getFogDensity();
        float shift = (float) (0.001F * event.getPartialTicks());
        fogDensityModifier -= shift;
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

    /**
     * Checks if the player is in direct contact with the fog and damages the player accordingly.
     * TODO: Make this more configurable. Ex. less damage in forests compared to deserts etc.
     * TODO: This currently works all the time, due to no way to check if there is fog or not…
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (FogWorldConfig.poisonousFog() && entity instanceof Player && !((Player) entity).isCreative()) {
            if (entity.tickCount > FogWorldConfig.poisonTicks())
                entity.hurt(FogWorld.DAMAGEFOG, FogWorldConfig.poisonDamage());
        }
    }
}