package com.pancake.tfc.skyblock.design.dataimport.services.process;

public enum ProcessType {

    CRAFTING_SHAPELESS ("minecraft:crafting_shapeless"),
    CRAFTING_SHAPED ("minecraft:crafting_shaped"),
    ADVANCED_SHAPELESS_CRAFTING ("tfc:advanced_shapeless_crafting"),
    ADVANCED_SHAPED_CRAFTING ("tfc:advanced_shaped_crafting"),
    CASTING ("tfc:casting"),
    ANVIL ("tfc:anvil"),
    ALLOY ("tfc:alloy"),
    POT_SOUP ("tfc:pot_soup"),
    BARREL_INSTANT ("tfc:barrel_instant"),
    WELDING ("tfc:welding"),
    COLLAPSE ("tfc:collapse"),
    SMELTING ("minecraft:smelting"),
    BARREL_SEALED ("tfc:barrel_sealed"),
    GLASSWORKING ("tfc:glassworking"),
    SCRAPING ("tfc:scraping"),
    BLASTING ("minecraft:blasting"),
    BLAST_FURNACE ("tfc:blast_furnace"),
    BARREL_INSANT_FLUID ("tfc:barrel_instant_fluid"),
    KNAPPING ("tfc:knapping"),
    LOOM ("tfc:loom"),
    QUERN ("tfc:quern"),
    BLOOMERY ("tfc:bloomery"),
    HEATING ("tfc:heating"),
    CLICKING_POT_WITH_BOWL ("custom:clicking_pot_with_bowl"),
    CLICKING_RAW_ROCK_WITH_HAMMER ("custom:clicking_raw_rock_with_hammer"),
    CHISEL ("tfc:chisel"),
    POT ("tfc:pot");

    public final String processType;

    ProcessType(String processType){
        this.processType = processType;
    }

    public String processType() {
        return processType;
    }

    public static ProcessType fromRecipeType(String type){
        for(ProcessType value : values()){
            if(value.processType().equals(type)){
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown process type " + type);
    }
}
