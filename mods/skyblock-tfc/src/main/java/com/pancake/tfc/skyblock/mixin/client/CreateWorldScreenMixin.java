package com.pancake.tfc.skyblock.mixin.client;

import com.pancake.tfc.skyblock.SkyblockTFC;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    private static final ResourceKey<WorldPreset> SKYBLOCK_PRESET =
            ResourceKey.create(
                    net.minecraft.core.registries.Registries.WORLD_PRESET,
                    ResourceLocation.fromNamespaceAndPath(
                            SkyblockTFC.MODID,
                            "overworld"
                    )
            );

    @Inject(
            method = "createNewWorldDirectory",
            at = @At("RETURN")
    )
    private void skyblocktfc$markSkyblockWorld(
            CallbackInfoReturnable<Optional<LevelStorageSource.LevelStorageAccess>> cir
    ) {
        Optional<LevelStorageSource.LevelStorageAccess> optional = cir.getReturnValue();

        if (optional.isEmpty()) {
            return;
        }

        CreateWorldScreen screen = (CreateWorldScreen) (Object) this;

        Holder<WorldPreset> preset =
                screen.getUiState().getWorldType().preset();

        if (preset == null || !preset.is(SKYBLOCK_PRESET)) {
            return;
        }

        LevelStorageSource.LevelStorageAccess storageAccess = optional.get();
        Path dataFolder = storageAccess
                .getLevelPath(LevelResource.ROOT)
                .resolve("data");
        Path skyblockMarker = SkyblockTFC.getSkyblockMarkerPath(dataFolder);

        try {
            Files.createDirectories(skyblockMarker.getParent());
            Files.createFile(skyblockMarker);

            SkyblockTFC.LOGGER.info(
                    "[SkyblockTFC] Marked world as SkyblockTFC: {}",
                    skyblockMarker
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to create SkyblockTFC world marker",
                    e
            );
        }
    }
}