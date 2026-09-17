package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AlloyRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.ALLOY);
    }

    @Override
    public boolean supportsFluids() {
        return true;
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        List<Set<String>> inputGroups = new ArrayList<>();

        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        for (JsonNode content : recipe.path("contents")) {
            inputGroups.add(Set.of(content.path("fluid").asString()));
        }

        String resultId = recipe.path("result").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                inputGroups,
                Set.of(resultId)
        );
    }
}