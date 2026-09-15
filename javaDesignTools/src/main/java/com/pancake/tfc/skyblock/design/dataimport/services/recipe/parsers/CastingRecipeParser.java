package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class CastingRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:casting";

    @Override
    public boolean supports(String recipeType) {
        return RECIPE_TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        String fluidId = recipe.path("fluid").path("fluid").asString();
        String resultId = recipe.path("result").path("id").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                List.of(Set.of(fluidId)),
                Set.of(resultId)
        );
    }
}