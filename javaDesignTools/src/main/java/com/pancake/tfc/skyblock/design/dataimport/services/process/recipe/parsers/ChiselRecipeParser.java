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
public class ChiselRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.CHISEL);
    }
    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        List<Set<String>> inputGroups = List.of(
                Set.of(ingredient.get(0).asString())
        );

        String result = recipe.get("result").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                inputGroups,
                Set.of(result)
        );
    }
}