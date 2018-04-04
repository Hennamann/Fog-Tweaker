package com.henrikstabell.fogworld;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
@Config(modid = FogWorld.MODID, name = "fogworld")
@Config.LangKey("fogworld.config.title")
public class FogConfig {

    @Config.Name("Fog Density")
    @Config.Comment("How much fog should the world have?")
    public static float fogDensity = 0.1F;

    @Config.Name("Fog Color")
    @Config.Comment("What color should the fog in the world have?")
    public static int fogColor = 16777215;

    @Mod.EventBusSubscriber(modid = FogWorld.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(FogWorld.MODID)) {
                ConfigManager.sync(FogWorld.MODID, Config.Type.INSTANCE);
            }
        }
    }

    public static float getFogDensity(int var1, int var2, int var3) {
        return fogDensity;
    }

    public static int getFogColor(int var1, int var2, int var3) {
        return fogColor;
    }
}