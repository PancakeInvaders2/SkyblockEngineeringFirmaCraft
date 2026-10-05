package com.pancake.tfc.triphammerpp.pickaxerendering;

// square shaft width x width x length
public record PickaxeShaftDimensions(
        int width, // square
        int length,
        int lengthAfterTheEye
) {}