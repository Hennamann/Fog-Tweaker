package com.henrikstabell.fogworld.handler;

import com.henrikstabell.fogworld.FogWorld;
import com.henrikstabell.fogworld.config.Configuration;
import com.henrikstabell.fogworld.config.biomesconfig.BiomeConfigReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FogWorld.MODID)
public class PoisonEventHandler {

    /**
     * Checks if the player is in direct contact with the fog and damages the player accordingly.
     * Uses the JSON configs to determine if damage should be dealt and how much and how often.
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Level world = entity.level;
        BlockPos pos = entity.blockPosition();

        if (BiomeConfigReader.doesBiomeConfigExist(world.getBiome(pos).getRegistryName())) {
            int poisonTicks = BiomeConfigReader.readBiomeConfig(world.getBiome(pos).getRegistryName()).getPoisonTicks();
            int poisonDamage = BiomeConfigReader.readBiomeConfig(world.getBiome(pos).getRegistryName()).getPoisonDamage();

            boolean fogEnabled = BiomeConfigReader.readBiomeConfig(world.getBiome(pos).getRegistryName()).isFogEnabled();
            boolean poisonEnabled = BiomeConfigReader.readBiomeConfig(world.getBiome(pos).getRegistryName()).isPoisonousFogEnabled();
            if (fogEnabled && poisonEnabled && Configuration.getPoisonousFogEnabled() && entity instanceof Player && !((Player) entity).isCreative()) {
                if (entity.tickCount > poisonTicks)
                    entity.hurt(FogWorld.DAMAGEFOG, poisonDamage);
            }
        }
    }
}