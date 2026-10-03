package com.pancake.tfc.skyblock.mixin;

import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(DedicatedServerSettings.class)
public interface DedicatedServerSettingsAccessor
{
    @Accessor("source")
    Path skyblocktfc$getSource();

    @Accessor("properties")
    DedicatedServerProperties skyblocktfc$getProperties();
}