package com.pancake.tfc.skyblock.mixin;

import com.pancake.tfc.skyblock.Config;
import com.pancake.tfc.skyblock.SkyblockTFC;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.pancake.tfc.skyblock.SkyblockTFC.LOGGER;

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
    private static void skyblocktfc$createStarterIsland(
            LevelEvent.CreateSpawnPosition event,
            CallbackInfo ci,
            ServerLevel level,
            ChunkGeneratorExtension extension,
            ChunkGenerator generator,
            ServerLevelData levelData,
            RandomSource random,
            ChunkPos chunkPos
    ) {
        if(SkyblockTFC.isSkyblockWorld()){
            BlockPos anchor = chunkPos.getWorldPosition()
                    .offset(8, generator.getSpawnHeight(level), 8);

            BlockPos spawnPos = placeStarterIsland(level, anchor);

            levelData.setSpawn(spawnPos, 0.0F);

            event.setCanceled(true);
            ci.cancel();
        }
    }

    private static BlockPos placeStarterIsland(ServerLevel level, BlockPos anchor) {



        ResourceLocation structureId = selectStarterIsland(level, anchor);

        StructureTemplateManager structureManager = level.getStructureManager();
        StructureTemplate structure = structureManager.getOrCreate(structureId);

        LOGGER.debug("[SkyblockTFC] placing starter island at {}", anchor);
        LOGGER.debug("[SkyblockTFC] structure size = {}", structure.getSize());
        LOGGER.debug("[SkyblockTFC] chunk loaded = {}", level.hasChunkAt(anchor));

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

        LOGGER.debug("[SkyblockTFC] placeInWorld returned {}", placed);
        LOGGER.debug("[SkyblockTFC] spawning player at {}", spawnPos);

        return spawnPos;
    }

    private static ResourceLocation selectStarterIsland(ServerLevel level, BlockPos anchor) {
        float temperature = ChunkData.get(level, anchor).getAverageSeaLevelTemp(anchor);

        for (String entry : Config.STARTER_ISLAND_VARIANTS.get()) {
            String[] parts = entry.split(":", 2);

            String structureName = parts[0];


            float maximumTemperature;
            if (parts.length == 1 // no ':' means no maximum temperature
                || temperature < (maximumTemperature = Float.parseFloat(parts[1])) ) {
                LOGGER.debug("[SkyblockTFC] Selecting structure {} for temperature = {}°C", structureName, temperature);

                return ResourceLocation.fromNamespaceAndPath(
                        SkyblockTFC.MODID,
                        structureName
                );
            }
        }

        throw new IllegalStateException(
                "No starter island variant covers temperature " + temperature
        );
    }
}