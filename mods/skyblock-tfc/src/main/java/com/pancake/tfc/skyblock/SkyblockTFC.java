package com.pancake.tfc.skyblock;

import com.pancake.tfc.skyblock.mixin.DedicatedServerAccessor;
import com.pancake.tfc.skyblock.mixin.DedicatedServerPropertiesAccessor;
import com.pancake.tfc.skyblock.mixin.DedicatedServerSettingsAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SkyblockTFC.MODID)
public class SkyblockTFC {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "skyblocktfc";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<WorldPreset> SKYBLOCK_PRESET =
            ResourceKey.create(
                    Registries.WORLD_PRESET,
                    ResourceLocation.fromNamespaceAndPath(
                            MODID,
                            "overworld"
                    )
            );

    private static String MARKER_FILENAME = ".skyblocktfc";
    private static boolean skyblockWorld;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SkyblockTFC(IEventBus modEventBus, ModContainer modContainer) {

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (SkyblockTFC) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        MinecraftServer server = event.getServer();
        LOGGER.info( "[SkyblockTFC] onServerAboutToStart MinecraftServer: {}", server );
        LOGGER.info( "[SkyblockTFC] onServerAboutToStart MinecraftServer canonicalName: {}", server.getClass().getCanonicalName());


        boolean configuredAsSkyblock = false;
        boolean markedAsSkyblock = false;
        if(event.getServer() instanceof DedicatedServer dedicatedServer){

            // we access levelType using Accessor mixins
            DedicatedServerSettings settings = ((DedicatedServerAccessor) server).skyblocktfc$getSettings();
            DedicatedServerProperties properties = ((DedicatedServerSettingsAccessor) settings).skyblocktfc$getProperties();
            DedicatedServerProperties.WorldDimensionData dimensionData = ((DedicatedServerPropertiesAccessor) properties).skyblocktfc$getWorldDimensionData();

            String levelType = dimensionData.levelType();
            LOGGER.info( "[SkyblockTFC] onServerAboutToStart levelType: {}", levelType );

            configuredAsSkyblock = (MODID+":overworld").equalsIgnoreCase(levelType);
            LOGGER.info("[SkyblockTFC] onServerAboutToStart configuredAsSkyblock: {}",configuredAsSkyblock);
        }
        else {

            // in the case of the net.minecraft.client.server.IntegratedServer
            // (when playing in a client directly)
            // the client mixin CreateWorldScreenMixin creates a marker file
            // to indicate that this is a tfcskyblock world

            // MinecraftServer is implemented by DedicatedServer, IntegratedServer, and GameTestServer
            // what happens with GameTestServer ?

            Path skyblockMarker = getSkyblockMarkerPath(
                    server.getWorldPath(LevelResource.ROOT)
                            .resolve("data")
            );

            markedAsSkyblock = Files.exists(skyblockMarker);
            LOGGER.info("[SkyblockTFC] onServerAboutToStart markedAsSkyblock: {}",markedAsSkyblock);
        }

        skyblockWorld = configuredAsSkyblock || markedAsSkyblock;
        LOGGER.info("[SkyblockTFC] onServerAboutToStart Skyblock world: {}",skyblockWorld);


    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event)
    {
        skyblockWorld = false;
    }



    public static boolean isSkyblockWorld() {
        return skyblockWorld;
    }

    public static Path getSkyblockMarkerPath(Path dataFolder){
        return dataFolder.resolve(MODID).resolve(MARKER_FILENAME);
    }
}
