package com.pancake.tfc.skyblock;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.nio.file.Files;
import java.nio.file.Path;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SkyblockTFC.MODID)
public class SkyblockTFC {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "skyblocktfc";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

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
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();

        Path skyblockMarker = getSkyblockMarkerPath(
                server.getWorldPath(LevelResource.ROOT)
                .resolve("data")
        );

        skyblockWorld = Files.exists(skyblockMarker);

        LOGGER.info(
                "[SkyblockTFC] onServerAboutToStart Skyblock world: {}",
                skyblockWorld
        );
    }

    public static Path getSkyblockMarkerPath(Path dataFolder){
        return dataFolder.resolve(MODID).resolve(MARKER_FILENAME);
    }

    public static boolean isSkyblockWorld() {
        return skyblockWorld;
    }
}
