package com.pancake.tfc.skyblock.mixin;

import net.dries007.tfc.world.TFCChunkGenerator;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TFCChunkGenerator.class)
public abstract class TFCChunkGeneratorMixin
{
    @Inject(
            method = "makeBedrock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skyblockengineeringfirmacraft$disableBedrock(
            ChunkAccess chunk,
            CallbackInfo ci
    )
    {
        ci.cancel();
    }
}