package com.pancake.tfc.skyblock.design.reachability.services;

public enum ScenarioEnum {

    VANILLA_TFC("vanilla_tfc"),
    SKYBLOCK_TFC("skyblock_tfc");

    private final String id;

    ScenarioEnum(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
