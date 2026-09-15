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
public class WeldingRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:welding";

    @Override
    public boolean supports(String recipeType) {
        return RECIPE_TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        List<Set<String>> inputGroups = new ArrayList<>();

        inputGroups.add(parseIngredient(
                recipe.path("first_input"),
                gameData
        ));

        inputGroups.add(parseIngredient(
                recipe.path("second_input"),
                gameData
        ));

        String resultId = recipe.path("result").path("id").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                inputGroups,
                Set.of(resultId)
        );
    }

    private Set<String> parseIngredient(JsonNode ingredient, GameData gameData) {
        if (ingredient.has("item")) {
            return Set.of(ingredient.path("item").asString());
        }

        if (ingredient.has("tag")) {
            return Set.copyOf(
                    gameData.itemTags().getOrDefault(
                            ingredient.path("tag").asString(),
                            List.of()
                    )
            );
        }

        throw new IllegalArgumentException(
                "Unsupported welding ingredient: " + ingredient
        );
    }
}