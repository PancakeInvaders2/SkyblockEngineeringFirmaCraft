package com.pancake.tfc.skyblock.design.dataimport.services.process;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record ParsedProcess(
        String id,
        String name,
        ProcessType type,
        Optional<ProcessSubtype> subtype,
        List<Set<String>> inputGroups, // outer set : AND; inner set : OR
        Set<String> outputs
) {
    public ParsedProcess(
        String id,
        String name,
        ProcessType type,
        List<Set<String>> inputGroups,
        Set<String> outputs
    ){
        this(id, name, type, Optional.empty(), inputGroups, outputs);
    }
}