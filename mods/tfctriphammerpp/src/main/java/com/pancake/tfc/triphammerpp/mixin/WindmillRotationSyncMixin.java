package com.pancake.tfc.triphammerpp.mixin;

import com.pancake.tfc.triphammerpp.TFCTripHammer;
import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.openjdk.nashorn.internal.runtime.logging.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WindmillBlockEntity.class)
public abstract class WindmillRotationSyncMixin {

    private static final int SYNC_INTERVAL = 100;

    @Inject(
            method = "serverTick",
            at = @At("HEAD")
    )
    private static void tfctriphammerpp$syncWindmillRotation(
            Level level,
            BlockPos pos,
            BlockState state,
            WindmillBlockEntity windmill,
            CallbackInfo ci
    ) {
        if (level.isClientSide) {
            return;
        }

        int offset = Math.floorMod(pos.hashCode(), SYNC_INTERVAL);
        if (Math.floorMod(level.getGameTime() + offset, SYNC_INTERVAL) == 0) {
            TFCTripHammer.LOGGER.info("Syncing windmill angle");
            // synchronize the angle once in a while even if not obstructed
            // so that server and client rotation angles are in-phase
            // and the pickaxe hit (server side) can happen when the
            // pickaxe rendering hits the rock (client side)
            windmill.markForSync();
        }
    }
}