package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import tools.jackson.databind.JsonNode;

import java.util.List;

public interface RecipeParser {

    default boolean supports(String recipeType){
        List<ProcessType> supportedRecipeTypes = getSupportedRecipeTypes();
        ProcessType processType = ProcessType.fromRecipeType(recipeType);
        return supportedRecipeTypes.contains(processType);
    }

    List<ProcessType> getSupportedRecipeTypes();

    ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData);

    default boolean supportsFluids(){
        return false;
    }
}