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
public class CastingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.CASTING);
    }

    @Override
    public boolean supportsFluids() {
        return true;
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        String fluidId = recipe.path("fluid").path("fluid").asString();
        String resultId = recipe.path("result").path("id").asString();

        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(Set.of(fluidId)),
                Set.of(resultId)
        );
    }
}