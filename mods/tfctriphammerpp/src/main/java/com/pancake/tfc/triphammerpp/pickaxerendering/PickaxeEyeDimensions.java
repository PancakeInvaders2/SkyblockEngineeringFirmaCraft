package com.pancake.tfc.triphammerpp.pickaxerendering;

// square collar around the square shaft.
// (thicknessAroundTheShaft + shaft.width ) x (thicknessAroundTheShaft + shaft.width ) x depth
public record PickaxeEyeDimensions(
        int thicknessAroundTheShaft, // in pixels
        int length
) {}