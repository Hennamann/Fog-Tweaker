package com.henrikstabell.fogworld.handler;

import com.henrikstabell.fogworld.FogWorld;
import com.henrikstabell.fogworld.config.Configuration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FogWorld.MODID)
public class PoisonEventHandler {

    /**
     * Checks if the player is in direct contact with the fog and damages the player accordingly.
     * TODO: Make this more configurable. Ex. less damage in forests compared to deserts etc.
     * TODO: This currently works all the time, due to no way to check if there is fog or not…
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (Configuration.poisonousFog() && entity instanceof Player && !((Player) entity).isCreative()) {
            if (entity.tickCount > Configuration.poisonTicks())
                entity.hurt(FogWorld.DAMAGEFOG, Configuration.poisonDamage());
        }
    }
}