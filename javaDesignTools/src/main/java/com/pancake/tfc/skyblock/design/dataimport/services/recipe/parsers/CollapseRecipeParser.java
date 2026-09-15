package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CollapseRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:collapse";

    @Override
    public boolean supports(String recipeType) {
        return RECIPE_TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        Set<String> inputs = new HashSet<>();

        for (JsonNode ingredient : recipe.path("ingredient")) {
            inputs.add(ingredient.asString());
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                List.of(inputs),
                Set.of(recipe.path("result").asString())
        );
    }
}