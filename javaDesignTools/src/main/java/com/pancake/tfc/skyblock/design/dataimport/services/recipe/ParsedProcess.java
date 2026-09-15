package com.pancake.tfc.skyblock.design.dataimport.services.recipe;

import java.util.List;
import java.util.Set;

public record ParsedProcess(
        String id,
        String name,
        String type,
        List<Set<String>> inputGroups, // outer set : AND; inner set : OR
        Set<String> outputs
) {}