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
public class ScrapingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.SCRAPING);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());


        if (ingredient == null
                || !ingredient.isObject()
                || !ingredient.get("item").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported scraping ingredient: " + ingredient
            );
        }

        String input = ingredient.get("item").asString();

        Set<String> outputs = new java.util.HashSet<>();

        JsonNode result = recipe.get("result");
        if (result != null && result.get("id") != null && result.get("id").isString()) {
            outputs.add(result.get("id").asString());
        }

        JsonNode resultItem = recipe.get("result_item");
        if (resultItem != null
                && resultItem.get("id") != null
                && resultItem.get("id").isString()) {
            outputs.add(resultItem.get("id").asString());
        }

        if (outputs.isEmpty()) {
            return null;
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(Set.of(input)),
                outputs
        );
    }
}