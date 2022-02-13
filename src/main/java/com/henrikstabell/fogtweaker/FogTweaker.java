package com.henrikstabell.fogtweaker;

import com.henrikstabell.fogtweaker.config.Configuration;
import com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfigWriter;
import com.henrikstabell.fogtweaker.config.biomeconfig.BiomeConfig;
import com.henrikstabell.fogtweaker.util.OptiFineUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.henrikstabell.fogtweaker.FogTweaker.MODID;

/**
 * See The repos LICENSE.MD file for what you can and can't do with the code.
 * Created by Hennamann(Ole Henrik Stabell) on 03/04/2018.
 */
@Mod(MODID)
public class FogTweaker {

    public static final String MODID = "fogtweaker";
    public static final DamageSource DAMAGEFOG = new DamageSource("fog");
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Set<ResourceLocation> biomeOverrides = new HashSet<>();

    public FogTweaker() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupDone);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.clientSpec, "fogtweaker/fogtweaker-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.commonSpec, "fogtweaker/fogtweaker-common.toml");
    }

    private void setupDone(final FMLLoadCompleteEvent event) {
        LOGGER.info("Fog Tweaker: Generating/Updating Biome configs…");
        BiomeConfigWriter.genBiomeConfigs();
        if (!biomeOverrides.isEmpty()) {
            LOGGER.info("Fog Tweaker: The following biomes are blacklisted and will not have fog: " + biomeOverrides);
        }
        LOGGER.info("Fog Tweaker: Reading Biome Configs…");
        BiomeConfig.readBiomeConfigs();
        LOGGER.info("Fog Tweaker: Finished Reading Biome Configs");
        LOGGER.info("Fog Tweaker: \"Load Complete\" Event Complete!");
    }

    private void processIMC(final InterModProcessEvent event) {
        Stream<InterModComms.IMCMessage> messages = event.getIMCStream();
        messages.forEach(message -> {
            String method = message.method();
            Object object = message.messageSupplier().get();
            if (method.equals("biome_override")) {
                biomeOverrides.add((ResourceLocation) object);
                LOGGER.info("Fog Tweaker: Registered Biome Override for " + object.toString() + ". Override requested by mod with modid: " + message.senderModId());
            }
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        List<String> incompatibleMods = List.of("");

        for (String incompatibleMod : incompatibleMods) {
            if (Configuration.getIncompatibleModsWarningEnabled()) {
                if (ModList.get().isLoaded(incompatibleMod)) {
                    IModInfo mod = ModList.get().getModFileById("fogtweaker").getMods().get(0);
                    LOGGER.warn("Fog Tweaker: Detected incompatible mod: " + incompatibleMod + " issuing warning!");
                    ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.COMMON_SETUP, "error.fogtweaker.incompatiblemod." + incompatibleMod, mod));
                }
            }
        }
        if (OptiFineUtil.isOptiFineLoaded()) {
            IModInfo mod = ModList.get().getModFileById("fogtweaker").getMods().get(0);
            LOGGER.warn("Fog Tweaker: Detected OptiFine issuing warning!");
            ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.COMMON_SETUP, "error.fogtweaker.incompatiblemod.optifine", mod));
        }
    }
}
