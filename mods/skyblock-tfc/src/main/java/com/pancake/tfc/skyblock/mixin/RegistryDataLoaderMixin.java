package com.pancake.tfc.skyblock.mixin;


import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;
import com.pancake.tfc.skyblock.SkyblockTFC;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

import static com.pancake.tfc.skyblock.SkyblockTFC.SKYBLOCK_PRESET;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {

    private static final ResourceKey<WorldPreset> TFC_PRESET =
            ResourceKey.create(
                    Registries.WORLD_PRESET,
                    ResourceLocation.fromNamespaceAndPath("tfc", "overworld")
            );



    @Inject(
            method = "loadContentsFromManager",
            at = @At("RETURN")
    )
    private static <E> void skyblocktfc$registerWorldPreset(
            ResourceManager resourceManager,
            RegistryOps.RegistryInfoLookup registryInfoLookup,
            WritableRegistry<E> registry,
            Decoder<E> codec,
            Map<ResourceKey<?>, Exception> loadingErrors,
            CallbackInfo ci
    ) {
        if (!registry.key().equals(Registries.WORLD_PRESET)) {
            return;
        }

        registerSkyblockPreset(registry);
    }

    @SuppressWarnings("unchecked")
    private static <E> void registerSkyblockPreset(
            WritableRegistry<E> registry
    ) {
        WritableRegistry<WorldPreset> worldPresetRegistry =
                (WritableRegistry<WorldPreset>) registry;

        WorldPreset tfcPreset =
                worldPresetRegistry.get(TFC_PRESET);

        if (tfcPreset == null) {
            throw new IllegalStateException(
                    "TFC Skyblock could not find " + TFC_PRESET
            );
        }

        if (worldPresetRegistry.containsKey(SKYBLOCK_PRESET)) {
            return;
        }

        WorldPreset skyblockPreset =
                new WorldPreset(
                        tfcPreset
                                .createWorldDimensions()
                                .dimensions()
                );

        worldPresetRegistry.register(
                SKYBLOCK_PRESET,
                skyblockPreset,
                new RegistrationInfo(
                        Optional.empty(),
                        Lifecycle.experimental()
                )
        );

        SkyblockTFC.LOGGER.info("[SkyblockTFC] Registered world preset {} from {}",
                SKYBLOCK_PRESET.location(),
                TFC_PRESET.location());
    }
}