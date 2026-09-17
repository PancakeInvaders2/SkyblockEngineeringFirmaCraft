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
public class LoomRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.LOOM);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        if (ingredient == null || !ingredient.isObject()) {
            throw new IllegalArgumentException(
                    "Unsupported loom ingredient: " + ingredient
            );
        }

        JsonNode item = ingredient.get("item");

        if (item == null || !item.isString()) {
            throw new IllegalArgumentException(
                    "Unsupported loom ingredient: " + ingredient
            );
        }

        JsonNode result = recipe.get("result");

        if (result == null
                || !result.isObject()
                || !result.get("id").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported loom result: " + result
            );
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(Set.of(item.asString())),
                Set.of(result.get("id").asString())
        );
    }
}