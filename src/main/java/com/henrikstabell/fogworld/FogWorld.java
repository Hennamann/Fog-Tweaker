package com.henrikstabell.fogworld;

import com.henrikstabell.fogworld.config.Configuration;
import com.henrikstabell.fogworld.config.biomesconfig.BiomeConfigGenerator;
import com.henrikstabell.fogworld.util.OptiFineUtil;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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

    public static final Set<ResourceLocation> biomeOverrides = new HashSet<>();

    public FogWorld() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupDone);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.clientSpec, "fogworld/fogworld-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.commonSpec, "fogworld/fogworld-common.toml");
    }

    private void setupDone(final FMLLoadCompleteEvent event) {
        LOGGER.info("Fog World: Generating/Updating Biome configs…");
        BiomeConfigGenerator.genBiomeConfigs();
        if (!biomeOverrides.isEmpty()) {
            LOGGER.info("Fog World: The following biomes are blacklisted and will not have fog: " + biomeOverrides);
        }
        LOGGER.info("Fog World: \"Load Complete\" Event Complete!");
    }

    private void processIMC(final InterModProcessEvent event) {
        Stream<InterModComms.IMCMessage> messages = event.getIMCStream();
        messages.forEach(message -> {
            String method = message.method();
            Object object = message.messageSupplier().get();
            if (method.equals("biome_override")) {
                biomeOverrides.add((ResourceLocation) object);
                LOGGER.info("Fog World: Registered Biome Override for " + object.toString() + ". Override requested by mod with modid: " + message.senderModId());
            }
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        List<String> incompatibleMods = List.of("mistcore");

        for (String incompatibleMod : incompatibleMods) {
            if (Configuration.getIncompatibleModsWarningEnabled()) {
                if (ModList.get().isLoaded(incompatibleMod)) {
                    IModInfo mod = ModList.get().getModFileById("fogworld").getMods().get(0);
                    LOGGER.warn("FogWorld: Detected incompatible mod: " + incompatibleMod + " issuing warning!");
                    ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.COMMON_SETUP, "error.fogworld.incompatiblemod." + incompatibleMod, mod));
                }
            }
        }
        if (OptiFineUtil.isOptiFineLoaded()) {
            IModInfo fogworld = ModList.get().getModFileById("fogworld").getMods().get(0);
            LOGGER.warn("FogWorld: Detected OptiFine issuing warning!");
            ModLoader.get().addWarning(new ModLoadingWarning(fogworld, ModLoadingStage.COMMON_SETUP, "error.fogworld.incompatiblemod.optifine", fogworld));
        }
    }
}
