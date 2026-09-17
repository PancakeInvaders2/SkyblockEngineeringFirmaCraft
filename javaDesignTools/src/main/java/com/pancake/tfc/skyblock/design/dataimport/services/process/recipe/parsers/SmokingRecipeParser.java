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
public class SmokingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.SMOKING);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");
        String input = ingredient.get("item").asString();
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

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
                processType,
                List.of(Set.of(input)),
                Set.of(output)
        );
    }
}