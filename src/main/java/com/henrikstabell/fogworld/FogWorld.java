package com.henrikstabell.fogworld;

import com.henrikstabell.fogworld.proxy.CommonProxy;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import static com.henrikstabell.fogworld.FogWorld.MODID;
import static com.henrikstabell.fogworld.FogWorld.VERSION;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
@Mod(modid = MODID, version = VERSION, certificateFingerprint = "@FINGERPRINT@")
public class FogWorld {

    public static final String MODID = "fogworld";
    public static final String VERSION = "@VERSION@";

    @SidedProxy(serverSide = "com.henrikstabell.proxy.CommonProxy", clientSide = "com.henrikstabell.fogworld.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static final DamageSource DAMAGEFOG = new DamageSource("fog");

    @Mod.EventHandler
    public static void onPostInit(FMLPostInitializationEvent event) {}
}