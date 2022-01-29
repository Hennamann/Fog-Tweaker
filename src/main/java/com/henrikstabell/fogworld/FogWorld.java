package com.henrikstabell.fogworld;

import com.henrikstabell.fogworld.config.Configuration;
import com.henrikstabell.fogworld.config.biomesconfig.BiomeConfigGenerator;
import com.henrikstabell.fogworld.util.OptiFineUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.henrikstabell.fogworld.FogWorld.MODID;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
@Mod(MODID)
public class FogWorld {

    public static final String MODID = "fogworld";
    public static final DamageSource DAMAGEFOG = new DamageSource("fog");
    public static final Logger LOGGER = LogManager.getLogger();


    public FogWorld() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupDone);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.clientSpec, "fogworld/fogworld-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.commonSpec, "fogworld/fogworld-common.toml");

    }

    private void setupDone(final FMLLoadCompleteEvent event) {
        LOGGER.info("Fog World: Generating/Updating Biome configs…");
        BiomeConfigGenerator.genBiomeConfigs();
    }

    private void setup(final FMLCommonSetupEvent event) {
        List<String> incompatibleMods = List.of("mistcore");

        for (String incompatibleMod : incompatibleMods) {
            if (Configuration.getIncompatibleModsWarningEnabled()) {
                if (ModList.get().isLoaded(incompatibleMod)) {
                    IModInfo mod = ModList.get().getModFileById("fogworld").getMods().get(0);
                    LOGGER.warn("FogWorld: Detected incompatible mod: " + incompatibleMod + " issuing warning!");
                    ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.CONSTRUCT, "error.fogworld.incompatiblemod." + incompatibleMod, mod));
                }
            }
        }
        if (OptiFineUtil.isOptiFineLoaded()) {
            IModInfo fogworld = ModList.get().getModFileById("fogworld").getMods().get(0);
            LOGGER.warn("FogWorld: Detected OptiFine issuing warning!");
            ModLoader.get().addWarning(new ModLoadingWarning(fogworld, ModLoadingStage.CONSTRUCT, "error.fogworld.incompatiblemod.optifine", fogworld));
        }
    }
}
