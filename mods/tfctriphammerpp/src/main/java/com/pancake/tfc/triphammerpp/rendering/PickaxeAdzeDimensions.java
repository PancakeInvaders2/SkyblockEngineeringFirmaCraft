package com.pancake.tfc.triphammerpp.rendering;


public record PickaxeAdzeDimensions(
        int length,
        float backwardSlopePixels,
        int bladeWidth, // x width
        float bladeThickness // z thickness
) {}