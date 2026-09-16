package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class CampfireCookingRecipeParser implements RecipeParser {

    private static final String TYPE = "minecraft:campfire_cooking";

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

        if (ingredient == null
                || !ingredient.isObject()
                || !ingredient.get("item").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported campfire cooking ingredient: " + ingredient
            );
        }

        JsonNode result = recipe.get("result");

        if (result == null
                || !result.isObject()
                || !result.get("id").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported campfire cooking result: " + result
            );
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                TYPE,
                List.of(Set.of(ingredient.get("item").asString())),
                Set.of(result.get("id").asString())
        );
    }
}