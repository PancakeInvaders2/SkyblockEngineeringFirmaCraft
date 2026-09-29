package com.pancake.tfc.skyblock.mixin;


import com.pancake.tfc.skyblock.SkyblockTFC;
import net.dries007.tfc.world.ChunkNoiseFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkNoiseFiller.class)
public abstract class ChunkNoiseFillerMixin
{
    @Redirect(
            method = "fillColumn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState skyblocktfc$preventTerrainBlockPlacement(
            LevelChunkSection section,
            int x,
            int y,
            int z,
            BlockState state,
            boolean lock
    )
    {
        if (SkyblockTFC.isSkyblockWorld()) {
            return section.getBlockState(x, y, z);
        }
        else{
            return section.setBlockState(x, y, z, state, lock);
        }
    }
}