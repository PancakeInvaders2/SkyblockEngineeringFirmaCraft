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
public class CollapseRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.COLLAPSE);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        Set<String> inputs = new HashSet<>();

        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        for (JsonNode ingredient : recipe.path("ingredient")) {
            inputs.add(ingredient.asString());
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(inputs),
                Set.of(recipe.path("result").asString())
        );
    }
}