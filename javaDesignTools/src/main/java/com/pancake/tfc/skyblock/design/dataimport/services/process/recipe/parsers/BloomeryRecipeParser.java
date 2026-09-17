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
public class BloomeryRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.BLOOMERY);
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
        JsonNode catalyst = recipe.get("catalyst");
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        if (catalyst == null
                || !catalyst.isObject()
                || !catalyst.get("item").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported bloomery catalyst: " + catalyst
            );
        }

        JsonNode fluid = recipe.get("fluid");
        if (fluid == null
                || !fluid.isObject()
                || !fluid.get("fluid").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported bloomery fluid: " + fluid
            );
        }

        JsonNode result = recipe.get("result");

        if (result == null
                || !result.isObject()
                || !result.get("id").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported bloomery result: " + result
            );
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(
                        Set.of(catalyst.get("item").asString()),
                        Set.of(fluid.get("fluid").asString())
                ),
                Set.of(result.get("id").asString())
        );
    }
}