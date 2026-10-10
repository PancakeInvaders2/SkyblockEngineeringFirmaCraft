package com.pancake.tfc.triphammerpp.pickaxerendering;


public record PickaxeAdzeDimensions(
        int length,
        float backwardSlopePixels,
        int bladeWidth, // x width
        float bladeThickness // z thickness
) {}