package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.inspector;

import tools.jackson.databind.JsonNode;

public record CountAndExample (int count, JsonNode example) {

    public CountAndExample increment(){
        return new CountAndExample(count + 1, example);
    }

    public CountAndExample withExample(JsonNode ex){
        return new CountAndExample(count, ex);
    }
}
