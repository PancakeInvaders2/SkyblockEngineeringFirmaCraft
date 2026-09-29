package com.pancake.tfc.skyblock.mixin;

import com.mojang.logging.LogUtils;
import com.pancake.tfc.skyblock.Config;
import com.pancake.tfc.skyblock.SkyblockEngineeringFirmaCraftGlueMod;
import net.dries007.tfc.ForgeEventHandler;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.pancake.tfc.skyblock.SkyblockEngineeringFirmaCraftGlueMod.LOGGER;

@Mixin(ForgeEventHandler.class)
public class ForgeEventHandlerMixin {

    @Inject(
            method = "onCreateWorldSpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/ServerLevelData;setSpawn(Lnet/minecraft/core/BlockPos;F)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void skyblock$createStarterIsland(
            LevelEvent.CreateSpawnPosition event,
            CallbackInfo ci,
            ServerLevel level,
            ChunkGeneratorExtension extension,
            ChunkGenerator generator,
            ServerLevelData levelData,
            RandomSource random,
            ChunkPos chunkPos
    ) {
        BlockPos anchor = chunkPos.getWorldPosition()
                .offset(8, generator.getSpawnHeight(level), 8);

        BlockPos spawnPos = placeStarterIsland(level, anchor);

        levelData.setSpawn(spawnPos, 0.0F);

        event.setCanceled(true);
        ci.cancel();
    }

    private static BlockPos placeStarterIsland(ServerLevel level, BlockPos anchor) {

        ResourceLocation structureId = selectStarterIsland(level, anchor);

        StructureTemplateManager structureManager = level.getStructureManager();
        StructureTemplate structure = structureManager.getOrCreate(structureId);

        LOGGER.debug("[Skyblock] placing starter island at {}", anchor);
        LOGGER.debug("[Skyblock] structure size = {}", structure.getSize());
        LOGGER.debug("[Skyblock] chunk loaded = {}", level.hasChunkAt(anchor));

        StructurePlaceSettings settings = new StructurePlaceSettings();

        boolean placed = structure.placeInWorld(
                level,
                anchor,
                anchor,
                settings,
                level.getRandom(),
                Block.UPDATE_ALL
        );

        BlockPos spawnPos = anchor.offset(
                Config.STARTER_SPAWN_X.get(),
                Config.STARTER_SPAWN_Y.get(),
                Config.STARTER_SPAWN_Z.get()
        );

        LOGGER.debug("[Skyblock] placeInWorld returned {}", placed);
        LOGGER.debug("[Skyblock] spawning player at {}", spawnPos);

        return spawnPos;
    }

    private static ResourceLocation selectStarterIsland(ServerLevel level, BlockPos anchor) {
        float temperature = ChunkData.get(level, anchor).getAverageSeaLevelTemp(anchor);

        for (String entry : Config.STARTER_ISLAND_VARIANTS.get()) {
            String[] parts = entry.split(":", 2);

            String structureName = parts[0];

            // no : means no maximum temperature
            if (parts.length == 1) {
                LOGGER.debug("[Skyblock] Selecting structure {} for temperature = {}°C", structureName, temperature);

                return ResourceLocation.fromNamespaceAndPath(
                        "skyblockengineeringfirmacraft",
                        structureName
                );
            }

            float maximumTemperature = Float.parseFloat(parts[1]);

            if (temperature < maximumTemperature) {
                LOGGER.debug("[Skyblock] Selecting structure {} for temperature = {}°C", structureName, temperature);

                return ResourceLocation.fromNamespaceAndPath(
                        "skyblockengineeringfirmacraft",
                        structureName
                );
            }
        }

        throw new IllegalStateException(
                "No starter island variant covers temperature " + temperature
        );
    }
}