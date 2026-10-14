package com.pancake.tfc.triphammerpp.rendering;

public record PickaxeDimensions(
        int distanceFromShaftEnd, // how much the shaft sticks past the head, in pixels
        PickaxeShaftDimensions shaft,
        PickaxeEyeDimensions eye, // part around the shaft
        PickaxePointyEndDimensions pointyEnd, // below the eye
        PickaxeAdzeDimensions adze // above the eye
) {}