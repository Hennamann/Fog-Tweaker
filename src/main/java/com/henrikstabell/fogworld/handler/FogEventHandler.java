package com.henrikstabell.fogworld.handler;

import com.henrikstabell.fogworld.FogWorld;
import com.henrikstabell.fogworld.api.interfaces.IBiomeFog;
import com.henrikstabell.fogworld.api.interfaces.IDimensionFog;
import com.henrikstabell.fogworld.config.FogWorldConfig;
import com.henrikstabell.fogworld.util.BiomeUtil;
import com.henrikstabell.fogworld.util.DimensionUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 * <p>
 * Renders customisable fog in biomes and dimensions not blacklisted in {@link FogWorldConfig#fogBiomeBlacklist} and {@link FogWorldConfig#fogDimensionBlacklist}
 */
@Mod.EventBusSubscriber(modid = FogWorld.MODID, value = {Side.CLIENT})
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
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            World world = player.world;
            int x = MathHelper.floor(player.posX);
            int y = MathHelper.floor(player.posY);
            int z = MathHelper.floor(player.posZ);
            IBlockState blockStateAtEyes = ActiveRenderInfo.getBlockStateAtEntityViewpoint(world, event.getEntity(), (float) event.getRenderPartialTicks());
            if (blockStateAtEyes.getMaterial() == Material.LAVA) {
                return;
            }
            Vec3d mixedColor;
            if (blockStateAtEyes.getMaterial() == Material.WATER) {
                mixedColor = getFogBlendColorWater(world, player, x, y, z, event.getRenderPartialTicks());
            } else {
                mixedColor = getFogBlendColor(world, player, x, y, z, event.getRed(), event.getGreen(), event.getBlue(), event.getRenderPartialTicks());
            }
            if (world.provider instanceof WorldProviderHell) {
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
     * Only renders in biomes and dimensions not in the blacklist {@link FogWorldConfig#fogBiomeBlacklist} {@link FogWorldConfig#fogDimensionBlacklist}
     */
    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Entity entity = event.getEntity();
        World world = entity.world;
        int playerX = MathHelper.floor(entity.posX);
        int playerY = MathHelper.floor(entity.posY);
        int playerZ = MathHelper.floor(entity.posZ);

        if ((double) playerX == fogX && (double) playerZ == fogZ && fogInit) {
            renderFog(event.getFogMode(), fogFarPlaneDistance, 0.75F);
        } else {
            fogInit = true;
            byte distance = 20;
            float fpDistanceBiomeFog = 0.0F;
            float weightBiomeFog = 0.0F;

            float farPlaneDistance;
            float farPlaneDistanceScaleBiome;
            for (int weightMixed = -distance; weightMixed <= distance; ++weightMixed) {
                for (int weightDefault = -distance; weightDefault <= distance; ++weightDefault) {
                    Biome biome = world.getBiomeForCoordsBody(new BlockPos(playerX + weightMixed, playerZ + weightDefault, playerY + weightDefault));
                    WorldProvider worldProvider = world.provider;
                    if (worldProvider instanceof IDimensionFog && !((IDimensionFog) worldProvider).getFogEnabled()) {
                        continue;
                    } else if (biome instanceof IBiomeFog && !((IBiomeFog) biome).getFogEnabled()) {
                        continue;
                    }
                    if (!DimensionUtil.isDimensionBlacklisted(worldProvider.getDimensionType())) {
                        if (!BiomeUtil.isBiomeBlacklisted(biome)) {
                            if (worldProvider instanceof IDimensionFog) {
                                farPlaneDistance = ((IDimensionFog) worldProvider).getFogDensity(playerX + weightMixed, playerY, playerZ + weightDefault);
                            } else if (biome instanceof IBiomeFog) {
                                farPlaneDistance = ((IBiomeFog) biome).getFogDensity(playerX + weightMixed, playerY, playerZ + weightDefault);
                            } else {
                                farPlaneDistance = FogWorldConfig.getFogDensity(playerX + weightMixed, playerY, playerZ + weightDefault);
                            }
                            if (worldProvider instanceof WorldProviderHell) {
                                farPlaneDistance = farPlaneDistance * 0.8F; // Adjust fog density for the nether
                            }

                            farPlaneDistanceScaleBiome = 1.0F;
                            double farPlaneDistanceScale;
                            if (weightMixed == -distance) {
                                farPlaneDistanceScale = 1.0D - (entity.posX - (double) playerX);
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            } else if (weightMixed == distance) {
                                farPlaneDistanceScale = entity.posX - (double) playerX;
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            }

                            if (weightDefault == -distance) {
                                farPlaneDistanceScale = 1.0D - (entity.posZ - (double) playerZ);
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            } else if (weightDefault == distance) {
                                farPlaneDistanceScale = entity.posZ - (double) playerZ;
                                farPlaneDistance = (float) ((double) farPlaneDistance * farPlaneDistanceScale);
                                farPlaneDistanceScaleBiome = (float) ((double) farPlaneDistanceScaleBiome * farPlaneDistanceScale);
                            }

                            fpDistanceBiomeFog += farPlaneDistance;
                            weightBiomeFog += farPlaneDistanceScaleBiome;
                        }
                    }
                }
            }

            float var17 = (float) (distance * 2 * distance * 2);
            float var18 = var17 - weightBiomeFog;
            float var19 = weightBiomeFog == 0.0F ? 0.0F : fpDistanceBiomeFog / weightBiomeFog;
            farPlaneDistance = (fpDistanceBiomeFog * 240.0F + event.getFarPlaneDistance() * var18) / var17;
            farPlaneDistanceScaleBiome = 0.1F * (1.0F - var19) + 0.75F * var19;
            float var20 = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75F * var18) / var17;
            fogX = entity.posX;
            fogZ = entity.posZ;
            fogFarPlaneDistance = Math.min(farPlaneDistance, event.getFarPlaneDistance());
            renderFog(event.getFogMode(), fogFarPlaneDistance, var20);
        }
    }

    /**
     * Renders the fog using OpenGL
     *
     * @param fogMode               int
     * @param farPlaneDistance      float
     * @param farPlaneDistanceScale float
     */
    private static void renderFog(int fogMode, float farPlaneDistance, float farPlaneDistanceScale) {
        if (fogMode < 0) {
            GL11.glFogf(GL11.GL_FOG_START, 0.0F);
            GL11.glFogf(GL11.GL_FOG_END, farPlaneDistance);
        } else {
            GL11.glFogf(GL11.GL_FOG_START, farPlaneDistance * farPlaneDistanceScale);
            GL11.glFogf(GL11.GL_FOG_END, farPlaneDistance);
        }
    }

    /**
     * Post processes the color to account for any potion effects or graphic options the player may have enabled.
     *
     * @param world              {@link World}
     * @param player             {@link EntityLivingBase}
     * @param r                  double
     * @param g                  double
     * @param b                  double
     * @param renderPartialTicks double
     * @return {@link Vec3d}
     */
    @MethodsReturnNonnullByDefault
    private static Vec3d postProcessColor(World world, EntityLivingBase player, double r, double g, double b, double renderPartialTicks) {
        double darkScale = (player.lastTickPosY + (player.posY - player.lastTickPosY) * renderPartialTicks) * world.provider.getVoidFogYFactor();
        if (player.isPotionActive(MobEffects.BLINDNESS)) {
            int duration = player.getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
            darkScale *= (duration < 20) ? (1 - duration / 20f) : 0;
        }

        if (darkScale < 1) {
            darkScale = (darkScale < 0) ? 0 : darkScale * darkScale;
            r *= darkScale;
            g *= darkScale;
            b *= darkScale;
        }

        if (player.isPotionActive(MobEffects.NIGHT_VISION)) {
            int duration = player.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
            float brightness = (duration > 200) ? 1 : 0.7f + MathHelper.sin((float) ((duration - renderPartialTicks) * Math.PI * 0.2f)) * 0.3f;

            double scale = 1 / r;
            scale = Math.min(scale, 1 / g);
            scale = Math.min(scale, 1 / b);

            r = r * (1 - brightness) + r * scale * brightness;
            g = g * (1 - brightness) + g * scale * brightness;
            b = b * (1 - brightness) + b * scale * brightness;
        }


        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            double aR = (r * 30 + g * 59 + b * 11) / 100;
            double aG = (r * 30 + g * 70) / 100;
            double aB = (r * 30 + b * 70) / 100;

            r = aR;
            g = aG;
            b = aB;
        }

        return new Vec3d(r, g, b);
    }

    /**
     * Gets the blend color for the fog when underwater
     *
     * @param world              {@link World}
     * @param playerEntity       {@link EntityLivingBase}
     * @param playerX            int
     * @param playerY            int
     * @param playerZ            int
     * @param renderPartialTicks double
     * @return {@link this#postProcessColor(World, EntityLivingBase, double, double, double, double)}
     */
    private static Vec3d getFogBlendColorWater(World world, EntityLivingBase playerEntity, int playerX, int playerY, int playerZ, double renderPartialTicks) {
        byte distance = 2;
        float rBiomeFog = 0.0F;
        float gBiomeFog = 0.0F;
        float bBiomeFog = 0.0F;

        float bMixed;
        for (int weight = -distance; weight <= distance; ++weight) {
            for (int respirationLevel = -distance; respirationLevel <= distance; ++respirationLevel) {
                Biome rMixed = world.getBiomeForCoordsBody(new BlockPos(playerX + weight, playerY + weight, playerZ + respirationLevel));
                int gMixed = rMixed.getWaterColorMultiplier();
                bMixed = (float) ((gMixed & 16711680) >> 16);
                float gPart = (float) ((gMixed & '\uff00') >> 8);
                float bPart = (float) (gMixed & 255);
                double zDiff;
                if (weight == -distance) {
                    zDiff = 1.0D - (playerEntity.posX - (double) playerX);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (weight == distance) {
                    zDiff = playerEntity.posX - (double) playerX;
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                }

                if (respirationLevel == -distance) {
                    zDiff = 1.0D - (playerEntity.posZ - (double) playerZ);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (respirationLevel == distance) {
                    zDiff = playerEntity.posZ - (double) playerZ;
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
        float var21 = (float) EnchantmentHelper.getRespirationModifier(playerEntity) * 0.2F;
        float var22 = (rBiomeFog * 0.02F + var21) / var20;
        float var23 = (gBiomeFog * 0.02F + var21) / var20;
        bMixed = (bBiomeFog * 0.2F + var21) / var20;
        return postProcessColor(world, playerEntity, var22, var23, bMixed, renderPartialTicks);
    }

    /**
     * Gets the normal blend color for the fog
     *
     * @param world              {@link World}
     * @param playerEntity       {@link EntityLivingBase}
     * @param playerX            int
     * @param playerY            int
     * @param playerZ            int
     * @param defR               float
     * @param defG               float
     * @param defB               float
     * @param renderPartialTicks double
     * @return {@link Vec3d}
     */
    private static Vec3d getFogBlendColor(World world, EntityLivingBase playerEntity, int playerX, int playerY, int playerZ, float defR, float defG, float defB, double renderPartialTicks) {
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        int[] ranges = ForgeModContainer.blendRanges;
        int distance = 0;
        if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length) {
            distance = ranges[settings.renderDistanceChunks];
        }

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
                Biome biome = world.getBiomeForCoordsBody(new BlockPos(playerX + celestialAngle, playerY + celestialAngle, playerZ + baseScale));
                WorldProvider worldProvider = world.provider;
                if (!DimensionUtil.isDimensionBlacklisted(worldProvider.getDimensionType())) {
                    if (!BiomeUtil.isBiomeBlacklisted(biome)) {
                        if (worldProvider instanceof IDimensionFog) {
                            bScale = ((IDimensionFog) worldProvider).getFogColor(playerX + celestialAngle, playerY, playerZ + baseScale);
                        } else if (biome instanceof IBiomeFog) {
                            bScale = ((IBiomeFog) biome).getFogColor(playerX + celestialAngle, playerY, playerZ + baseScale);
                        } else {
                            bScale = FogWorldConfig.getFogColor(playerX + celestialAngle, playerY, playerZ + baseScale);
                        }
                        rainStrength = (float) ((bScale & 16711680) >> 16);
                        thunderStrength = (float) ((bScale & '\uff00') >> 8);
                        float processedColor = (float) (bScale & 255);
                        weightMixed = 1.0F;
                        double weightDefault;
                        if (celestialAngle == -distance) {
                            weightDefault = 1.0D - (playerEntity.posX - (double) playerX);
                            rainStrength = (float) ((double) rainStrength * weightDefault);
                            thunderStrength = (float) ((double) thunderStrength * weightDefault);
                            processedColor = (float) ((double) processedColor * weightDefault);
                            weightMixed = (float) ((double) weightMixed * weightDefault);
                        } else if (celestialAngle == distance) {
                            weightDefault = playerEntity.posX - (double) playerX;
                            rainStrength = (float) ((double) rainStrength * weightDefault);
                            thunderStrength = (float) ((double) thunderStrength * weightDefault);
                            processedColor = (float) ((double) processedColor * weightDefault);
                            weightMixed = (float) ((double) weightMixed * weightDefault);
                        }

                        if (baseScale == -distance) {
                            weightDefault = 1.0D - (playerEntity.posZ - (double) playerZ);
                            rainStrength = (float) ((double) rainStrength * weightDefault);
                            thunderStrength = (float) ((double) thunderStrength * weightDefault);
                            processedColor = (float) ((double) processedColor * weightDefault);
                            weightMixed = (float) ((double) weightMixed * weightDefault);
                        } else if (baseScale == distance) {
                            weightDefault = playerEntity.posZ - (double) playerZ;
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
            }
        }

        if (weightBiomeFog == 0.0F) {
            return new Vec3d((double) defR, (double) defG, (double) defB);
        } else {
            rBiomeFog /= 255.0F;
            gBiomeFog /= 255.0F;
            bBiomeFog /= 255.0F;
            float var28 = world.getCelestialAngle((float) renderPartialTicks);
            float var29 = MathHelper.clamp(MathHelper.cos(var28 * 3.1415927F * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
            float var30 = var29 * 0.94F + 0.06F;
            float var31 = var29 * 0.94F + 0.06F;
            float var32 = var29 * 0.91F + 0.09F;
            rainStrength = world.getRainStrength((float) renderPartialTicks);
            if (rainStrength > 0.0F) {
                var30 *= 1.0F - rainStrength * 0.5F;
                var31 *= 1.0F - rainStrength * 0.5F;
                var32 *= 1.0F - rainStrength * 0.4F;
            }

            thunderStrength = world.getThunderStrength((float) renderPartialTicks);
            if (thunderStrength > 0.0F) {
                var30 *= 1.0F - thunderStrength * 0.5F;
                var31 *= 1.0F - thunderStrength * 0.5F;
                var32 *= 1.0F - thunderStrength * 0.5F;
            }

            rBiomeFog *= var30 / weightBiomeFog;
            gBiomeFog *= var31 / weightBiomeFog;
            bBiomeFog *= var32 / weightBiomeFog;
            Vec3d var33 = postProcessColor(world, playerEntity, rBiomeFog, gBiomeFog, bBiomeFog, renderPartialTicks);
            rBiomeFog = (float) var33.x;
            gBiomeFog = (float) var33.y;
            bBiomeFog = (float) var33.z;
            weightMixed = (float) (distance * 2 * distance * 2);

            float var34 = weightMixed - weightBiomeFog;
            double rFinal = (double) ((rBiomeFog * weightBiomeFog + defR * var34) / weightMixed);
            double gFinal = (double) ((gBiomeFog * weightBiomeFog + defG * var34) / weightMixed);
            double bFinal = (double) ((bBiomeFog * weightBiomeFog + defB * var34) / weightMixed);

            return new Vec3d(rFinal, gFinal, bFinal);
        }
    }

    /**
     * Checks if the player is in direct contact with the fog and damages the player accordingly.
     * TODO: Make this more configurable. Ex. less damage in forests compared to deserts etc.
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;
        if (FogWorldConfig.poisonousFog && entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative() && !DimensionUtil.isDimensionBlacklisted(world.provider.getDimensionType()) && !BiomeUtil.isBiomeBlacklisted(world.getBiome(new BlockPos(((EntityPlayer) entity).posX, ((EntityPlayer) entity).posY, ((EntityPlayer) entity).posZ))) && ((EntityPlayer) entity).ticksExisted > FogWorldConfig.posionTicks && !(world.provider instanceof IDimensionFog) && !(world.getBiome(new BlockPos(((EntityPlayer) entity).posX, ((EntityPlayer) entity).posY, ((EntityPlayer) entity).posZ)) instanceof IBiomeFog)) {
            if (world.getLightFor(EnumSkyBlock.SKY, entity.getPosition()) > 10) {
                entity.attackEntityFrom(FogWorld.DAMAGEFOG, FogWorldConfig.poisonDamage);
            }
        }
    }
}
