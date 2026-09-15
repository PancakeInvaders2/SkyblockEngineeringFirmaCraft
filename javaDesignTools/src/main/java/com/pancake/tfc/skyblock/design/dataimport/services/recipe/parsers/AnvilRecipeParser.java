package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class AnvilRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:anvil";

    @Override
    public boolean supports(String recipeType) {
        return RECIPE_TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        JsonNode ingredient = recipe.path("ingredient");

        Set<String> inputResources;

        if (ingredient.has("item")) {
            inputResources = Set.of(ingredient.path("item").asString());
        } else if (ingredient.has("tag")) {
            inputResources = Set.copyOf(
                    gameData.itemTags()
                            .getOrDefault(
                                    ingredient.path("tag").asString(),
                                    List.of()
                            )
            );
        } else {
            throw new IllegalArgumentException(
                    "Unsupported anvil ingredient in " + recipeId + ": " + ingredient
            );
        }

        String resultId = recipe.path("result").path("id").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                List.of(inputResources),
                Set.of(resultId)
        );
    }
}