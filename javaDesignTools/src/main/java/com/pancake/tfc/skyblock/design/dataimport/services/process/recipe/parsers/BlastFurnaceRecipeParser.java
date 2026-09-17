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
public class BlastFurnaceRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.BLAST_FURNACE);
    }

    @Override
    public boolean supportsFluids() {
        return true;
    }


    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        List<Set<String>> inputGroups = new ArrayList<>();
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        JsonNode catalyst = recipe.get("catalyst");

        if (catalyst == null
                || !catalyst.isObject()
                || !catalyst.get("item").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported blast furnace catalyst: " + catalyst
            );
        }

        inputGroups.add(Set.of(catalyst.get("item").asString()));

        JsonNode fluid = recipe.get("fluid");

        if (fluid == null
                || !fluid.isObject()
                || !fluid.get("fluid").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported blast furnace fluid: " + fluid
            );
        }

        inputGroups.add(Set.of(fluid.get("fluid").asString()));

        JsonNode result = recipe.get("result");

        if (result == null
                || !result.isObject()
                || !result.get("id").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported blast furnace result: " + result
            );
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                inputGroups,
                Set.of(result.get("id").asString())
        );
    }
}