package com.pancake.tfc.triphammerpp.rendering;

// square collar around the square shaft.
// (thicknessAroundTheShaft + shaft.width ) x (thicknessAroundTheShaft + shaft.width ) x depth
public record PickaxeEyeDimensions(
        float thicknessAroundTheShaft, // in pixels
        int length
) {}