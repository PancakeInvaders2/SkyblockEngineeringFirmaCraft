package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BlastingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.BLASTING);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        if (ingredient == null) {
            throw new IllegalArgumentException(
                    "Missing blasting ingredient: " + recipe
            );
        }

        Set<String> inputAlternatives = new HashSet<>();

        if (ingredient.isObject()) {
            JsonNode item = ingredient.get("item");

            if (item == null || !item.isString()) {
                throw new IllegalArgumentException(
                        "Unsupported blasting ingredient: " + ingredient
                );
            }

            inputAlternatives.add(item.asString());

        } else if (ingredient.isArray()) {
            for (JsonNode alternative : ingredient) {
                if (!alternative.isObject()) {
                    throw new IllegalArgumentException(
                            "Unsupported blasting ingredient alternative: " + alternative
                    );
                }

                JsonNode item = alternative.get("item");

                if (item == null || !item.isString()) {
                    throw new IllegalArgumentException(
                            "Unsupported blasting ingredient alternative: " + alternative
                    );
                }

                inputAlternatives.add(item.asString());
            }

        } else {
            throw new IllegalArgumentException(
                    "Unsupported blasting ingredient: " + ingredient
            );
        }

        if (inputAlternatives.isEmpty()) {
            throw new IllegalArgumentException(
                    "Empty blasting ingredient: " + ingredient
            );
        }

        JsonNode result = recipe.get("result");

        if (result == null
                || !result.isObject()
                || !result.get("id").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported blasting result: " + result
            );
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(inputAlternatives),
                Set.of(result.get("id").asString())
        );
    }
}