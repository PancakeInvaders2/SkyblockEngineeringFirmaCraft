package com.pancake.tfc.triphammerpp.pickaxerendering;


public record PickaxeAdzeDimensions(
        int length,
        int backwardSlopePixels,
        int bladeWidth, // x width
        int bladeThickness // z thickness
) {}