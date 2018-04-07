package com.henrikstabell.fogworld.core;

import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

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

    @Config.Name("Poison Fog")
    @Config.Comment("Should the fog be poisonous?")
    public static boolean poisonousFog = false;

    @Config.Name("Poison Fog Delay")
    @Config.Comment("How many ticks before the player takes damage from the poisonous fog? 1 second = 20 ticks")
    public static int posionTicks = 1200;

    @Config.Name("Disabled Biomes")
    @Config.Comment("A list of disabled biomes, add a name to this list and the biome will not render with fog.")
    public static String[] fogBiomeBlacklist = {};

    @Config.Name("Disabled Dimensions")
    @Config.Comment("A list of disabled dimensions, add a name to this list and the dimension will not render with fog.")
    public static String[] fogDimensionBlacklist = {};

    public static float getFogDensity(int var1, int var2, int var3) {
        return fogDensity;
    }

    public static int getFogColor(int var1, int var2, int var3) {
        return fogColor;
    }

    public static List<String> getFogBiomeBlacklist() {
        return Lists.newArrayList(fogBiomeBlacklist);
    }

    public static List<String> getFogDimenstionBlacklist() {
        return Lists.newArrayList(fogDimensionBlacklist);
    }

    @Mod.EventBusSubscriber(modid = FogWorld.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(FogWorld.MODID)) {
                ConfigManager.sync(FogWorld.MODID, Config.Type.INSTANCE);
            }
        }
    }
}