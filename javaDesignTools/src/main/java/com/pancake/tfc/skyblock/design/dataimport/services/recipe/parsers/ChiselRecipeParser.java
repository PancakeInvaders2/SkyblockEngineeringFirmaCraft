package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class ChiselRecipeParser implements RecipeParser {

    @Override
    public boolean supports(String recipeType) {
        return "tfc:chisel".equals(recipeType);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");

        List<Set<String>> inputGroups = List.of(
                Set.of(ingredient.get(0).asString())
        );

        String result = recipe.get("result").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                "tfc:chisel",
                inputGroups,
                Set.of(result)
        );
    }
}