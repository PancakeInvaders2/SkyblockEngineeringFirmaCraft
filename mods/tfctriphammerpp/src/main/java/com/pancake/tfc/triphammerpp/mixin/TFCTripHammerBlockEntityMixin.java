package com.pancake.tfc.triphammerpp.mixin;

import com.pancake.tfc.triphammerpp.TFCTripHammer;
import com.pancake.tfc.triphammerpp.TripHammerPP;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(TripHammerBlockEntity.class)
public class TFCTripHammerBlockEntityMixin
{

    private int tfctriphammerpp$destroyProgress;

    public int tfctriphammerpp$getDestroyProgress() {
        return tfctriphammerpp$destroyProgress;
    }

    public void tfctriphammerpp$setDestroyProgress(int destroyProgress) {
        tfctriphammerpp$destroyProgress = destroyProgress;
    }

    @Inject(
            method = "serverTick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void tfctriphammerpp$test(
            Level level,
            BlockPos pos,
            BlockState state,
            TripHammerBlockEntity hammer,
            CallbackInfo ci,

            int cooldown,
            Rotation rotation,
            float angle,
            ItemStack item,
            float lastAngle,
            float minAngle,
            float maxAngle,
            BlockPos targetPos // anvilPos in the original code
    ) {
        TFCTripHammer.LOGGER.debug("[TFCTripHammer++] in TripHammerBlockEntityMixin");

        BlockEntity target = level.getBlockEntity(targetPos);
        if (target instanceof AnvilBlockEntity && TripHammerPP.canItemWorkAnvil(item)) {
            return; // continue the vanilla tfc code
        }

        if (TripHammerPP.isTargetRock(level, targetPos, target) && TripHammerPP.canItemBreakRock(item) ) {
            TripHammerPP.handleRock(
                    level,
                    pos,
                    targetPos,
                    hammer,
                    item,
                    rotation
            );
        }
        else if ( ! TripHammerPP.isTargetEmpty(level, targetPos, target) ) {
            // invalid setup so it breaks
            level.destroyBlock(pos, true);
        }

    }

}