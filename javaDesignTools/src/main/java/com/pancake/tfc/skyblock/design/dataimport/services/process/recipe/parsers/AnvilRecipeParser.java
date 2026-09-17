package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class AnvilRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.ANVIL);
    }


    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {

        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        JsonNode ingredient = recipe.path("ingredient");

        Set<String> inputResources;

        if (ingredient.has("item")) {
            inputResources = Set.of(ingredient.path("item").asString());
        } else if (ingredient.has("tag")) {
            inputResources = Set.copyOf(
                    gameData.itemTags()
                            .getOrDefault(
                                    ingredient.path("tag").asString(),
                                    Set.of()
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
                processType,
                List.of(inputResources),
                Set.of(resultId)
        );
    }
}