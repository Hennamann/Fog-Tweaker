package com.henrikstabell.fogworld.handler;

import com.henrikstabell.fogworld.FogWorld;
import com.henrikstabell.fogworld.config.FogWorldConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.MethodsReturnNonnullByDefault;
import com.mojang.math.Vector3d;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 * <p>
 * Renders customisable fog in biomes and dimensions.
 */
@Mod.EventBusSubscriber(modid = FogWorld.MODID, value = {Dist.CLIENT})
public class FogEventHandler {

    private static double fogX;
    private static double fogZ;
    private static boolean fogInit;
    private static float fogFarPlaneDistance;

    /**
     * Calculates the fog color to be used when rendering the fog.
     */
    @SubscribeEvent
    public static void onGetFogColor(EntityViewRenderEvent.FogColors event) {
        if (event.getCamera().getEntity() instanceof Player) {
            Player player = (Player) event.getCamera().getEntity();
            Level world = player.level;
            int x = Mth.floor(player.xo);
            int y = Mth.floor(player.yo);
            int z = Mth.floor(player.zo);
            BlockState blockStateAtEyes = event.getCamera().getBlockAtCamera();
            if (blockStateAtEyes.getMaterial() == Material.LAVA) {
                return;
            }
            Vector3d mixedColor;
            if (blockStateAtEyes.getMaterial() == Material.WATER) {
                mixedColor = getFogBlendColorWater(world, player, x, y, z, event.getPartialTicks());
            } else {
                mixedColor = getFogBlendColor(world, player, x, y, z, event.getRed(), event.getGreen(), event.getBlue(), event.getPartialTicks());
            }
            if (world.dimension() == Level.NETHER) {
                // Amplify color for to counteract darkened fog colors in the nether.
                event.setRed((float) mixedColor.x * 20.5F);
                event.setGreen((float) mixedColor.y * 20.5F);
                event.setBlue((float) mixedColor.z * 20.5F);
            } else {
                event.setRed((float) mixedColor.x);
                event.setGreen((float) mixedColor.y);
                event.setBlue((float) mixedColor.z);
            }
        }
    }

    /**
     * Renders the fog based on the color and density set in {@link FogWorldConfig}
     */
    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Entity entity = event.getCamera().getEntity();
        Level world = entity.level;
        int playerX = Mth.floor(entity.xo);
        int playerY = Mth.floor(entity.yo);
        int playerZ = Mth.floor(entity.zo);

        if ((double) playerX == fogX && (double) playerZ == fogZ && fogInit) {
            renderFog(event.getMode(), fogFarPlaneDistance, 0.75F);
        } else {
            fogInit = true;
            byte distance = 20;
            float fpDistanceBiomeFog = 0.0F;
            float weightBiomeFog = 0.0F;

            float farPlaneDistance;
            float farPlaneDistanceScaleBiome;
            for (int weightMixed = -distance; weightMixed <= distance; ++weightMixed) {
                for (int weightDefault = -distance; weightDefault <= distance; ++weightDefault) {
                    Biome biome = world.getBiome(new BlockPos(playerX + weightMixed, playerZ + weightDefault, playerY + weightDefault));
                            farPlaneDistance = FogWorldConfig.getFogDensity();
                            if (world.dimension() == Level.NETHER ) {
                                farPlaneDistance = farPlaneDistance * 0.8F; // Adjust fog density for the nether
                            }

                            farPlaneDistanceScaleBiome = 1.0F;
                            double farPlaneDistanceScale;
                            if (weightMixed == -distance) {
                                farPlaneDistanceScale = 1.0D - (entity.xo - (double) playerX);
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            } else if (weightMixed == distance) {
                                farPlaneDistanceScale = entity.xo - (double) playerX;
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            }

                            if (weightDefault == -distance) {
                                farPlaneDistanceScale = 1.0D - (entity.zo - (double) playerZ);
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            } else if (weightDefault == distance) {
                                farPlaneDistanceScale = entity.zo - (double) playerZ;
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            }

                            fpDistanceBiomeFog += farPlaneDistance;
                            weightBiomeFog += farPlaneDistanceScaleBiome;
                        }
                    }

            float var17 = (float) (distance * 2 * distance * 2);
            float var18 = var17 - weightBiomeFog;
            float var19 = weightBiomeFog == 0.0F ? 0.0F : fpDistanceBiomeFog / weightBiomeFog;
            farPlaneDistance = (fpDistanceBiomeFog * 240.0F + event.getFarPlaneDistance() * var18) / var17;
            farPlaneDistanceScaleBiome = 0.1F * (1.0F - var19) + 0.75F * var19;
            float var20 = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75F * var18) / var17;
            fogX = entity.xo;
            fogZ = entity.zo;
            fogFarPlaneDistance = Math.min(farPlaneDistance, event.getFarPlaneDistance());
            renderFog(event.getMode(), fogFarPlaneDistance, var20);
        }
    }

    /**
     * Renders the fog using OpenGL
     *
     * @param fogMode               int
     * @param farPlaneDistance      float
     * @param farPlaneDistanceScale float
     */
    private static void renderFog(FogRenderer.FogMode fogMode, float farPlaneDistance, float farPlaneDistanceScale) {
        if (fogMode == FogRenderer.FogMode.FOG_SKY) {
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(farPlaneDistance);
        } else {
            RenderSystem.setShaderFogStart(farPlaneDistance * farPlaneDistanceScale);
            RenderSystem.setShaderFogEnd(farPlaneDistance);
        }
    }

    /**
     * Post processes the color to account for any potion effects or graphic options the player may have enabled.
     *
     * @param world              {@link Level}
     * @param player             {@link LivingEntity}
     * @param r                  double
     * @param g                  double
     * @param b                  double
     * @param renderPartialTicks double
     * @return {@link Vector3d}
     */
    @MethodsReturnNonnullByDefault
    private static Vector3d postProcessColor(Level world, LivingEntity player, double r, double g, double b, double renderPartialTicks) {
        double darkScale = (player.xOld + (player.yo - player.yOld) * renderPartialTicks) * world.getSkyDarken();
        if (player.hasEffect(MobEffects.BLINDNESS)) {
            int duration = player.getEffect(MobEffects.BLINDNESS).getDuration();
            darkScale *= (duration < 20) ? (1 - duration / 20f) : 0;
        }

        if (darkScale < 1) {
            darkScale = (darkScale < 0) ? 0 : darkScale * darkScale;
            r *= darkScale;
            g *= darkScale;
            b *= darkScale;
        }

        if (player.hasEffect(MobEffects.NIGHT_VISION)) {
            int duration = player.getEffect(MobEffects.NIGHT_VISION).getDuration();
            float brightness = (duration > 200) ? 1 : 0.7f + Mth.sin((float) ((duration - renderPartialTicks) * Math.PI * 0.2f)) * 0.3f;

            double scale = 1 / r;
            scale = Math.min(scale, 1 / g);
            scale = Math.min(scale, 1 / b);

            r = r * (1 - brightness) + r * scale * brightness;
            g = g * (1 - brightness) + g * scale * brightness;
            b = b * (1 - brightness) + b * scale * brightness;
        }

        return new Vector3d(r, g, b);
    }

    /**
     * Gets the blend color for the fog when underwater
     *
     * @param world              {@link Level}
     * @param playerEntity       {@link LivingEntity}
     * @param playerX            int
     * @param playerY            int
     * @param playerZ            int
     * @param renderPartialTicks double
     * @return {@link this#postProcessColor(Level, LivingEntity, double, double, double, double)}
     */
    private static Vector3d getFogBlendColorWater(Level world, LivingEntity playerEntity, int playerX, int playerY, int playerZ, double renderPartialTicks) {
        byte distance = 2;
        float rBiomeFog = 0.0F;
        float gBiomeFog = 0.0F;
        float bBiomeFog = 0.0F;

        float bMixed;
        for (int weight = -distance; weight <= distance; ++weight) {
            for (int respirationLevel = -distance; respirationLevel <= distance; ++respirationLevel) {
                Biome rMixed = world.getBiome(new BlockPos(playerX + weight, playerY + weight, playerZ + respirationLevel));
                int gMixed = rMixed.getWaterColor();
                bMixed = (float) ((gMixed & 16711680) >> 16);
                float gPart = (float) ((gMixed & '\uff00') >> 8);
                float bPart = (float) (gMixed & 255);
                double zDiff;
                if (weight == -distance) {
                    zDiff = 1.0D - (playerEntity.xo - (double) playerX);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (weight == distance) {
                    zDiff = playerEntity.xo - (double) playerX;
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                }

                if (respirationLevel == -distance) {
                    zDiff = 1.0D - (playerEntity.zo - (double) playerZ);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (respirationLevel == distance) {
                    zDiff = playerEntity.zo - (double) playerZ;
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                }

                rBiomeFog += bMixed;
                gBiomeFog += gPart;
                bBiomeFog += bPart;
            }
        }

        rBiomeFog /= 255.0F;
        gBiomeFog /= 255.0F;
        bBiomeFog /= 255.0F;
        float var20 = (float) (distance * 2 * distance * 2);
        float var21 = (float) EnchantmentHelper.getRespiration(playerEntity) * 0.2F;
        float var22 = (rBiomeFog * 0.02F + var21) / var20;
        float var23 = (gBiomeFog * 0.02F + var21) / var20;
        bMixed = (bBiomeFog * 0.2F + var21) / var20;
        return postProcessColor(world, playerEntity, var22, var23, bMixed, renderPartialTicks);
    }

    /**
     * Gets the normal blend color for the fog
     *
     * @param world              {@link Level}
     * @param playerEntity       {@link LivingEntity}
     * @param playerX            int
     * @param playerY            int
     * @param playerZ            int
     * @param defR               float
     * @param defG               float
     * @param defB               float
     * @param renderPartialTicks double
     * @return {@link Vector3d}
     */
    private static Vector3d getFogBlendColor(Level world, LivingEntity playerEntity, int playerX, int playerY, int playerZ, float defR, float defG, float defB, double renderPartialTicks) {
        int distance = 0;

        float rBiomeFog = 0.0F;
        float gBiomeFog = 0.0F;
        float bBiomeFog = 0.0F;
        float weightBiomeFog = 0.0F;

        float rainStrength;
        float thunderStrength;
        float weightMixed;
        int bScale;
        for (int celestialAngle = -distance; celestialAngle <= distance; ++celestialAngle) {
            for (int baseScale = -distance; baseScale <= distance; ++baseScale) {
                Biome biome = world.getBiome(new BlockPos(playerX + celestialAngle, playerY + celestialAngle, playerZ + baseScale));
                        bScale = FogWorldConfig.fogColor();
                        rainStrength = (float) ((bScale & 16711680) >> 16);
                        thunderStrength = (float) ((bScale & '\uff00') >> 8);
                        float processedColor = (float) (bScale & 255);
                        weightMixed = 1.0F;
                        double weightDefault;
                        if (celestialAngle == -distance) {
                            weightDefault = 1.0D - (playerEntity.xo - (double) playerX);
                            rainStrength = (float) ((double) rainStrength * weightDefault);
                            thunderStrength = (float) ((double) thunderStrength * weightDefault);
                            processedColor = (float) ((double) processedColor * weightDefault);
                            weightMixed = (float) ((double) weightMixed * weightDefault);
                        } else if (celestialAngle == distance) {
                            weightDefault = playerEntity.xo - (double) playerX;
                            rainStrength = (float) ((double) rainStrength * weightDefault);
                            thunderStrength = (float) ((double) thunderStrength * weightDefault);
                            processedColor = (float) ((double) processedColor * weightDefault);
                            weightMixed = (float) ((double) weightMixed * weightDefault);
                        }

                        if (baseScale == -distance) {
                            weightDefault = 1.0D - (playerEntity.zo - (double) playerZ);
                            rainStrength = (float) ((double) rainStrength * weightDefault);
                            thunderStrength = (float) ((double) thunderStrength * weightDefault);
                            processedColor = (float) ((double) processedColor * weightDefault);
                            weightMixed = (float) ((double) weightMixed * weightDefault);
                        } else if (baseScale == distance) {
                            weightDefault = playerEntity.zo - (double) playerZ;
                            rainStrength = (float) ((double) rainStrength * weightDefault);
                            thunderStrength = (float) ((double) thunderStrength * weightDefault);
                            processedColor = (float) ((double) processedColor * weightDefault);
                            weightMixed = (float) ((double) weightMixed * weightDefault);
                        }

                        rBiomeFog += rainStrength;
                        gBiomeFog += thunderStrength;
                        bBiomeFog += processedColor;
                        weightBiomeFog += weightMixed;
                    }
                }

        if (weightBiomeFog == 0.0F) {
            return new Vector3d((double) defR, (double) defG, (double) defB);
        } else {
            rBiomeFog /= 255.0F;
            gBiomeFog /= 255.0F;
            bBiomeFog /= 255.0F;
            float var28 = world.getSunAngle((float) renderPartialTicks);
            float var29 = Mth.clamp(Mth.cos(var28 * 3.1415927F * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
            float var30 = var29 * 0.94F + 0.06F;
            float var31 = var29 * 0.94F + 0.06F;
            float var32 = var29 * 0.91F + 0.09F;
            rainStrength = world.getRainLevel((float) renderPartialTicks);
            if (rainStrength > 0.0F) {
                var30 *= 1.0F - rainStrength * 0.5F;
                var31 *= 1.0F - rainStrength * 0.5F;
                var32 *= 1.0F - rainStrength * 0.4F;
            }

            thunderStrength = world.getThunderLevel((float) renderPartialTicks);
            if (thunderStrength > 0.0F) {
                var30 *= 1.0F - thunderStrength * 0.5F;
                var31 *= 1.0F - thunderStrength * 0.5F;
                var32 *= 1.0F - thunderStrength * 0.5F;
            }

            rBiomeFog *= var30 / weightBiomeFog;
            gBiomeFog *= var31 / weightBiomeFog;
            bBiomeFog *= var32 / weightBiomeFog;
            Vector3d var33 = postProcessColor(world, playerEntity, rBiomeFog, gBiomeFog, bBiomeFog, renderPartialTicks);
            rBiomeFog = (float) var33.x;
            gBiomeFog = (float) var33.y;
            bBiomeFog = (float) var33.z;
            weightMixed = (float) (distance * 2 * distance * 2);

            float var34 = weightMixed - weightBiomeFog;
            double rFinal = (double) ((rBiomeFog * weightBiomeFog + defR * var34) / weightMixed);
            double gFinal = (double) ((gBiomeFog * weightBiomeFog + defG * var34) / weightMixed);
            double bFinal = (double) ((bBiomeFog * weightBiomeFog + defB * var34) / weightMixed);

            return new Vector3d(rFinal, gFinal, bFinal);
        }
    }

    /**
     * Checks if the player is in direct contact with the fog and damages the player accordingly.
     * TODO: Make this more configurable. Ex. less damage in forests compared to deserts etc.
     * TODO: Fix this in 1.18.1…
     */
    //@SubscribeEvent
    //public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
    //    LivingEntity entity = event.getEntityLiving();
    //    Level world = entity.level;
    //    if (FogWorldConfig.poisonousFog && entity instanceof Player && !((Player) entity).isCreative() && !DimensionUtil.isDimensionBlacklisted(world.dimensionType()) && !BiomeUtil.isBiomeBlacklisted(world.getBiome(new BlockPos(((Player) entity).xo, ((Player) entity).yo, ((Player) entity).zo))) && ((Player) entity).tickCount > FogWorldConfig.posionTicks && !(world.dimensionType() instanceof IDimensionFog) && !(world.getBiome(new BlockPos(((Player) entity).xo, ((Player) entity).yo, ((Player) entity).zo)) instanceof IBiomeFog)) {
    //        if (world.getLightEmission(Blocks.AIR, entity.getPosition()) > 10) {
    //            entity.hurt(FogWorld.DAMAGEFOG, FogWorldConfig.poisonDamage);
    //        }
    //    }
    //}
}
