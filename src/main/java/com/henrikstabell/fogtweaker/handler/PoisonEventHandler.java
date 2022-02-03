package com.henrikstabell.fogtweaker.handler;

import com.henrikstabell.fogtweaker.FogTweaker;
import com.henrikstabell.fogtweaker.config.Configuration;
import com.henrikstabell.fogtweaker.config.biomesconfig.BiomeConfigReader;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FogTweaker.MODID)
public class PoisonEventHandler {

    /**
     * Checks if the Biome the entity is in is a fog biome and hurts the player if fog poison is enabled for the biome.
     * Uses the JSON configs to determine if damage should be dealt and how much and how often.
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Level world = entity.level;
        BlockPos pos = entity.blockPosition();
        ResourceLocation biome = world.getBiome(pos).getRegistryName();

        if (Configuration.getPoisonousFogEnabled()) {
            if (!FogTweaker.biomeOverrides.contains(biome)) {
                assert biome != null;
                if (BiomeConfigReader.doesBiomeConfigExist(biome)) {
                    int poisonTicks = BiomeConfigReader.readBiomeConfig(biome).getPoisonTicks();
                    int poisonDamage = BiomeConfigReader.readBiomeConfig(biome).getPoisonDamage();
                    boolean poisonEnabled = BiomeConfigReader.readBiomeConfig(biome).isPoisonousFogEnabled();

                    if (poisonEnabled && !((Player) entity).isCreative()) {
                        if (entity.tickCount > poisonTicks)
                            entity.hurt(FogTweaker.DAMAGEFOG, poisonDamage);
                    }
                }
            }
        }
    }
}