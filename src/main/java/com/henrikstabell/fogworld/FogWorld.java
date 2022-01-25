package com.henrikstabell.fogworld;

import com.henrikstabell.fogworld.config.Configuration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import static com.henrikstabell.fogworld.FogWorld.MODID;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
@Mod(MODID)
public class FogWorld {

    public static final String MODID = "fogworld";

    public static final DamageSource DAMAGEFOG = new DamageSource("fog");

    public FogWorld() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CONFIG_SPEC);
    }

}
