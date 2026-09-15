package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AlloyRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:alloy";

    @Override
    public boolean supports(String recipeType) {
        return RECIPE_TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        List<Set<String>> inputGroups = new ArrayList<>();

        for (JsonNode content : recipe.path("contents")) {
            inputGroups.add(Set.of(content.path("fluid").asString()));
        }

        String resultId = recipe.path("result").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                inputGroups,
                Set.of(resultId)
        );
    }
}