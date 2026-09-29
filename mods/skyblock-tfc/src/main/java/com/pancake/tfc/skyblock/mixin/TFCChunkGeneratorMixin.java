package com.pancake.tfc.skyblock.mixin;

import com.pancake.tfc.skyblock.SkyblockTFC;
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
    private void skyblocktfc$disableBedrock(
            ChunkAccess chunk, CallbackInfo ci) {
        if (SkyblockTFC.isSkyblockWorld()) {
            // cancel bedrock creation in skyblock worlds
            ci.cancel();
        }
    }
}