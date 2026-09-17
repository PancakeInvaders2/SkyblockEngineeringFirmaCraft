package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class PotRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.POT);
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


        JsonNode fluidIngredient = recipe.get("fluid_ingredient");

        if (fluidIngredient != null) {
            String fluidId = fluidIngredient.get("fluid").asString();
            inputGroups.add(Set.of(fluidId));
        }

        JsonNode ingredients = recipe.get("ingredients");

        if (ingredients != null) {
            for (JsonNode ingredient : ingredients) {
                inputGroups.add(parseIngredient(ingredient));
            }
        }

        Set<String> outputs = new HashSet<>();

        JsonNode itemOutput = recipe.get("item_output");

        if (itemOutput != null) {
            for (JsonNode output : itemOutput) {
                if (output.has("id")) {
                    outputs.add(output.get("id").asString());
                }
            }
        }

        JsonNode fluidOutput = recipe.get("fluid_output");

        if (fluidOutput != null) {
            outputs.add(fluidOutput.get("id").asString());
        }

        if (outputs.isEmpty()) {
            return null;
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                inputGroups,
                outputs
        );
    }

    private Set<String> parseIngredient(JsonNode ingredient) {
        if (ingredient.has("item")) {
            return Set.of(ingredient.get("item").asString());
        }

        if ("tfc:and".equals(ingredient.get("type").asString())) {
            Set<String> resources = new HashSet<>();

            for (JsonNode child : ingredient.get("children")) {
                if (child.has("item")) {
                    resources.add(child.get("item").asString());
                }
            }

            return resources;
        }

        throw new IllegalArgumentException(
                "Unexpected tfc:pot ingredient: " + ingredient
        );
    }
}