package com.pancake.tfc.skyblock.design.dataimport.services.recipe;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import tools.jackson.databind.JsonNode;

public interface RecipeParser {

    boolean supports(String recipeType);

    ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData);
}