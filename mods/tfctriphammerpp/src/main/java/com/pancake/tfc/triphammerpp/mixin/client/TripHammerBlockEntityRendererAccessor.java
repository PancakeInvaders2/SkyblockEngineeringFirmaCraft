package com.pancake.tfc.triphammerpp.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.dries007.tfc.client.render.blockentity.TripHammerBlockEntityRenderer")
public interface TripHammerBlockEntityRendererAccessor
{
    @Invoker("getPivotAngle")
    static float tfctriphammerpp$invokeGetPivotAngle(
            boolean isNegative,
            float angle,
            float startAngleOffset,
            float apexAngle,
            float endAngleOffset
    ) {
        throw new AssertionError();
    }
}