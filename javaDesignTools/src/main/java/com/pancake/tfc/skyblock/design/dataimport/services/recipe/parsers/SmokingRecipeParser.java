package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class SmokingRecipeParser implements RecipeParser {

    @Override
    public boolean supports(String recipeType) {
        return "minecraft:smoking".equals(recipeType);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");
        String input = ingredient.get("item").asString();

        String output = recipe
                .get("result")
                .get("id")
                .asString();

        if (!"minecraft:dried_kelp".equals(output)) {
            return null;
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                "minecraft:smoking",
                List.of(Set.of(input)),
                Set.of(output)
        );
    }
}