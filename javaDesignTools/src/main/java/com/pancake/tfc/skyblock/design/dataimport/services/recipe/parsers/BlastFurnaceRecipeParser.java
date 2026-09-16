package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class BlastFurnaceRecipeParser implements RecipeParser {

    private static final String TYPE = "tfc:blast_furnace";

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
        List<Set<String>> inputGroups = new ArrayList<>();

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
                TYPE,
                inputGroups,
                Set.of(result.get("id").asString())
        );
    }
}