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
public class BlastingRecipeParser implements RecipeParser {

    private static final String TYPE = "minecraft:blasting";

    @Override
    public boolean supports(String recipeType) {
        return TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");

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
                TYPE,
                List.of(inputAlternatives),
                Set.of(result.get("id").asString())
        );
    }
}