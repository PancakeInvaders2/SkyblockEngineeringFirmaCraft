package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Component
public class BarrelInstantRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:barrel_instant";

    @Override
    public boolean supports(String recipeType) {
        return RECIPE_TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        List<Set<String>> inputGroups = new ArrayList<>();
        Set<String> outputs = new HashSet<>();

        JsonNode inputFluid = recipe.path("input_fluid").path("fluid");
        if (!inputFluid.isMissingNode()) {
            inputGroups.add(Set.of(inputFluid.asString()));
        }

        JsonNode inputItem = recipe.path("input_item");
        if (inputItem.has("item")) {
            inputGroups.add(Set.of(inputItem.path("item").asString()));
        }

        JsonNode outputFluid = recipe.path("output_fluid").path("id");
        if (!outputFluid.isMissingNode()) {
            outputs.add(outputFluid.asString());
        }

        JsonNode outputItem = recipe.path("output_item").path("id");
        if (!outputItem.isMissingNode()) {
            outputs.add(outputItem.asString());
        }

        if (outputs.isEmpty()) {
            return null;
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                inputGroups,
                outputs
        );
    }
}