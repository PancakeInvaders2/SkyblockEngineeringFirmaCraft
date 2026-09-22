package com.pancake.tfc.skyblock.design.dataimport.services.process;

import java.util.*;

public record ParsedProcess(
        String id,
        String name,
        ProcessType type,
        Optional<ProcessSubtype> subtype,

        List<Set<String>> inputGroups, // outer set : AND; inner set : OR
        Set<String> outputs
) {
    public ParsedProcess(){
        this(null, null, null, Optional.empty(), new ArrayList<>(), new HashSet<>());
    }


    public ParsedProcess(
        String id,
        String name,
        ProcessType type,
        List<Set<String>> inputGroups,
        Set<String> outputs
    ){
        this(id, name, type, Optional.empty(), inputGroups, outputs);
    }

    public ParsedProcess withId(String id){
        return new ParsedProcess(
                id,
                name,
                type,
                subtype,
                inputGroups,
                outputs );
    }

    public ParsedProcess withName(String name){
        return new ParsedProcess(
                id,
                name,
                type,
                subtype,
                inputGroups,
                outputs );
    }


    public ParsedProcess withType(ProcessType type){
        return new ParsedProcess(
                id,
                name,
                type,
                subtype,
                inputGroups,
                outputs );
    }
    public ParsedProcess withSubtype(Optional<ProcessSubtype> subtype){
        return new ParsedProcess(
                id,
                name,
                type,
                subtype,
                inputGroups,
                outputs );
    }
    public ParsedProcess withInputGroups(List<Set<String>> inputGroups){
        return new ParsedProcess(
                id,
                name,
                type,
                subtype,
                inputGroups,
                outputs );
    }
    public ParsedProcess withOutputs(Set<String> outputs){
        return new ParsedProcess(
                id,
                name,
                type,
                subtype,
                inputGroups,
                outputs );
    }


}