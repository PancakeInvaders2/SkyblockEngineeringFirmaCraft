package com.pancake.tfc.triphammerpp.mixin;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.pancake.tfc.triphammerpp.logic.server.TripHammerPP;
import com.pancake.tfc.triphammerpp.TripHammerProgressAccess;
import com.z1z1y2.tfcfasttriphammer.mixin.TripHammerBlockEntityAccessor;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.common.blocks.TripHammerBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(
        value = TripHammerBlockEntity.class,
        // 1000 is the default, this makes us be applied after com.z1z1y2.tfcfasttriphammer.mixin.TripHammerBlockEntityMixin
        priority = 900
)
public class TFCFastTripHammerBlockEntityMixin implements TripHammerProgressAccess
{

    private static final AtomicInteger NEXT_BREAKER_ID = new AtomicInteger();

    @Unique
    private final int tfctriphammerpp$breakerId =
            -NEXT_BREAKER_ID.incrementAndGet();

    @Unique
    private float tfctriphammerpp$destroyProgress;

    @Override
    @Unique
    public float tfctriphammerpp$getDestroyProgress()
    {
        return tfctriphammerpp$destroyProgress;
    }

    @Override
    @Unique
    public void tfctriphammerpp$setDestroyProgress(float destroyProgress)
    {
        tfctriphammerpp$destroyProgress = destroyProgress;
    }

    @Override
    @Unique
    public int tfctriphammerpp$getBreakerId() {
        return tfctriphammerpp$breakerId;
    }

    @Inject(
            method = "serverTick",
            at = @At("HEAD")
    )
    private static void tfctriphammerpp$validateBreakingTarget (
            Level level,
            BlockPos pos,
            BlockState state,
            TripHammerBlockEntity hammer,
            CallbackInfo ci
    ) {
        // checks on every tick, so that if another source breaks the block, the progress can correctly start from 0
        TripHammerPP.resetProgressIfTargetIsMissing( level, hammer);

    }

    @WrapMethod(method = "triggerWork")
    private static void tfctriphammerpp$triggerWork(
            Level level,
            BlockPos pos,
            BlockState state,
            TripHammerBlockEntity hammer,
            Rotation rotation,
            ItemStack item,
            TripHammerBlockEntityAccessor accessor,
            boolean isCrossBlade,
            Operation<Void> original
    ) {

        /*
         * Trip Hammer only performs target work when the hammer is
         * rotating in its positive direction. Preserve its existing
         * negative-direction behavior by letting it run.
         */
        boolean isPositive =
                rotation.positiveDirection()
                        == state.getValue(TripHammerBlock.FACING).getClockWise();
        if (!isPositive)
        {
            original.call(level,pos, state, hammer, rotation, item, accessor, isCrossBlade);
            return;
        }


        BlockPos targetPos = TripHammerPP.getTargetPosition(hammer);
        boolean worked = false;

        BlockEntity target = level.getBlockEntity(targetPos);
        if (target instanceof AnvilBlockEntity && TripHammerPP.isItemAHammer(item)) {
            // Anvil working with a hammer retains Fast Trip Hammer's behavior.
            original.call(level, pos, state, hammer, rotation, item, accessor, isCrossBlade);
            return;
        }
        else if (TripHammerPP.isTargetRock(level, targetPos) && TripHammerPP.isItemAPickaxe(item) ) {
            worked = TripHammerPP.handleRock(level, pos, targetPos, hammer, item, rotation);
        }
        else if (TripHammerPP.isTargetEmpty(level, targetPos)){
            // nothing to do
        }
        else {
            // hammer + rock -> break the trip hammer
            // pickaxe + anvil -> break the trip hammer
            // etc
            level.destroyBlock(pos, true); // invalid setup, break the trip hammer
        }

        if (worked) {
            Helpers.damageItem(item, level);
            hammer.markForSync();
        }

        if (item.isEmpty()) {
            level.playSound((Player)null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS);
        }

        float multiplier = isCrossBlade ? 0.2f : 0.8f;
        int cooldownTicks = Mth.ceil(multiplier * Mth.TWO_PI / rotation.positiveSpeed());
        accessor.setCooldownTicks(cooldownTicks);

    }
}