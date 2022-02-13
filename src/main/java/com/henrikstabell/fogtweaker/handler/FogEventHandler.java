package com.henrikstabell.fogtweaker.handler;

import com.henrikstabell.fogtweaker.FogTweaker;
import com.henrikstabell.fogtweaker.config.Configuration;
import com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = FogTweaker.MODID, value = Dist.CLIENT)
public class FogEventHandler {

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event) {
        Level world = event.getCamera().getEntity().level;
        BlockPos pos = event.getCamera().getBlockPosition();
        ResourceLocation biome = world.getBiome(pos).getRegistryName();

        if (!FogTweaker.biomeOverrides.contains(biome)) {
            if (Configuration.getFogEnabled()) {
                if (BiomeConfig.getBiomeConfigFor(biome).isFogEnabled()) {
                        String fogColorString = BiomeConfig.getBiomeConfigFor(biome).getFogColor();
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

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Level world = event.getCamera().getEntity().level;
        BlockPos pos = event.getCamera().getBlockPosition();
        ResourceLocation biome = world.getBiome(pos).getRegistryName();

        FogType fogtype = event.getCamera().getFluidInCamera();
        Entity entity = event.getCamera().getEntity();

        if (Configuration.getFogEnabled()) {
            if (!FogTweaker.biomeOverrides.contains(biome)) {
                    if (BiomeConfig.getBiomeConfigFor(biome).isFogEnabled()) {
                        float configFogDensity = BiomeConfig.getBiomeConfigFor(biome).getFogDensity();
                        float f2;
                        float f3;
                        if (fogtype == FogType.LAVA) {
                            if (entity.isSpectator()) {
                                f2 = -8.0F;
                                f3 = configFogDensity * 0.5F;
                            } else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
                                f2 = 0.0F;
                                f3 = 3.0F;
                            } else {
                                f2 = 0.25F;
                                f3 = 1.0F;
                            }
                        } else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasEffect(MobEffects.BLINDNESS)) {
                            int i = Objects.requireNonNull(((LivingEntity) entity).getEffect(MobEffects.BLINDNESS)).getDuration();
                            float f1 = Mth.lerp(Math.min(1.0F, (float) i / 20.0F), configFogDensity, 5.0F);
                            if (event.getMode() == FogRenderer.FogMode.FOG_SKY) {
                                f2 = 0.0F;
                                f3 = f1 * 0.8F;
                            } else {
                                f2 = f1 * 0.25F;
                                f3 = f1;
                            }
                        } else if (fogtype == FogType.POWDER_SNOW) {
                            if (entity.isSpectator()) {
                                f2 = -8.0F;
                                f3 = configFogDensity * 0.5F;
                            } else {
                                f2 = 0.0F;
                                f3 = 2.0F;
                            }
                        } else if (event.getMode() == FogRenderer.FogMode.FOG_SKY) {
                            f2 = 0.0F;
                            f3 = configFogDensity;
                        } else {
                            float f4 = Mth.clamp(configFogDensity / 10.0F, 4.0F, 64.0F);
                            f2 = configFogDensity - f4;
                            f3 = configFogDensity;
                        }
                        RenderSystem.setShaderFogStart(f2);
                        RenderSystem.setShaderFogEnd(f3);
                    }
            }
        }
        if (!FogTweaker.biomeOverrides.contains(biome)) {
                if (Configuration.getFogParticlesEnabled() && BiomeConfig.getBiomeConfigFor(biome).isParticlesEnabled() && !Minecraft.getInstance().isPaused()) {
                    Random random = event.getCamera().getEntity().getLevel().getRandom();
                    for (int i = 0; i < BiomeConfig.getBiomeConfigFor(biome).getParticleAmount(); i++) {
                        Vec3 vec = event.getCamera().getEntity().position().add(0, random.nextDouble() * 3D, 0).add(new Vec3(random.nextDouble() * 3D, 0D, 0D).yRot((float) Math.toRadians(random.nextInt(360))));
                        event.getCamera().getEntity().level.addParticle((ParticleOptions) Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(BiomeConfig.getBiomeConfigFor(biome).getParticleType()))), vec.x, vec.y, vec.z, 0, 0, 0);
                    }
                }
        }
    }
}
