package com.pancake.tfc.triphammerpp.mixin;

import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TripHammerBlockEntity.class)
public interface TFCTripHammerBlockEntityAccessor
{
    @Accessor("cooldownTicks")
    void tfctriphammerpp$setCooldownTicks(int value);

    @Accessor("tfctriphammerpp$destroyProgress")
    int tfctriphammerpp$getDestroyProgress();

    @Accessor("tfctriphammerpp$destroyProgress")
    void tfctriphammerpp$setDestroyProgress(int value);
}