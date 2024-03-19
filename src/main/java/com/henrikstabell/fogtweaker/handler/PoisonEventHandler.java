package com.henrikstabell.fogtweaker.handler;

import com.henrikstabell.fogtweaker.FogTweaker;
import com.henrikstabell.fogtweaker.config.Configuration;
import com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = FogTweaker.MODID, value = Dist.CLIENT)
public class PoisonEventHandler {

    /**
     * Checks if the Biome the entity is in is a fog biome and hurts the player if fog poison is enabled for the biome.
     * Uses the JSON configs to determine if damage should be dealt and how much and how often.
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level world = entity.level;
        BlockPos pos = entity.blockPosition();
        ResourceLocation biome = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(world.getBiome(pos).get());
        if (Configuration.getPoisonousFogEnabled()) {
            if (!FogTweaker.BIOME_OVERRIDES.contains(biome)) {
                int poisonTicks = BiomeConfig.getBiomeConfigFor(biome).getPoisonTicks();
                int poisonDamage = BiomeConfig.getBiomeConfigFor(biome).getPoisonDamage();
                boolean poisonEnabled = BiomeConfig.getBiomeConfigFor(biome).isPoisonousFogEnabled();

                if (poisonEnabled) {
                    if (entity.tickCount > poisonTicks)
                        if (entity instanceof Player) {
                            if (!((Player) entity).isCreative()) {
                                entity.hurt(FogTweaker.FOG_DAMAGE_SOURCE, poisonDamage);
                            }
                        }
                    entity.hurt(FogTweaker.FOG_DAMAGE_SOURCE, poisonDamage);
                }
            }
        }
    }
}