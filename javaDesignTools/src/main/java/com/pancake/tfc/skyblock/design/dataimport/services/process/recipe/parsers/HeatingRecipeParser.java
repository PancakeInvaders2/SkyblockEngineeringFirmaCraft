package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class HeatingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.HEATING);
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
        List<Set<String>> inputGroups = parseInput(recipe, gameData);
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());


        if (!recipe.has("result_item") && !recipe.has("result_fluid")) {
            return null;
        }

        Set<String> outputs = parseOutput(recipe);

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                inputGroups,
                outputs
        );
    }

    private List<Set<String>> parseInput(
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");

        if (ingredient == null) {
            throw new IllegalArgumentException(
                    "Heating recipe " + recipe
                            + " has no ingredient"
            );
        }

        return parseIngredient(ingredient, gameData);
    }
    private List<Set<String>> parseIngredient(
            JsonNode ingredient,
            GameData gameData
    ) {
        if (ingredient.isArray()) {
            Set<String> alternatives = new HashSet<>();

            for (JsonNode alternative : ingredient) {
                List<Set<String>> groups =
                        parseIngredient(alternative, gameData);

                for (Set<String> group : groups) {
                    alternatives.addAll(group);
                }
            }

            return List.of(alternatives);
        }

        if ("tfc:and".equals(ingredient.path("type").asString())) {
            List<Set<String>> groups = new ArrayList<>();

            for (JsonNode child : ingredient.get("children")) {
                groups.addAll(parseIngredient(child, gameData));
            }

            return groups;
        }

        if (ingredient.has("item")) {
            return List.of(Set.of(
                    ingredient.get("item").asString()
            ));
        }

        if (ingredient.has("tag")) {
            String tag = ingredient.get("tag").asString();

            Set<String> items = gameData.itemTags().get(tag);

            if (items == null) {
                throw new IllegalArgumentException(
                        "Unknown item tag: " + tag
                );
            }

            return List.of(Set.copyOf(items));
        }

        // Conditions such as tfc:not_rotten don't represent
        // a resource input.
        return List.of();
    }
    private Set<String> parseOutput(JsonNode recipe) {
        if (recipe.has("result_item")) {
            JsonNode resultItem = recipe.get("result_item");

            if (resultItem.has("stack")) {
                resultItem = resultItem.get("stack");
            }

            JsonNode id = resultItem.get("id");

            if (id == null || !id.isString()) {
                throw new IllegalArgumentException(
                        "Heating recipe result_item has no string id: " + recipe
                );
            }

            return Set.of(id.asString());
        }

        if (recipe.has("result_fluid")) {
            JsonNode resultFluid = recipe.get("result_fluid");

            JsonNode id = resultFluid.get("id");

            if (id == null || !id.isString()) {
                throw new IllegalArgumentException(
                        "Heating recipe result_fluid has no string id: " + recipe
                );
            }

            return Set.of(id.asString());
        }

        throw new IllegalArgumentException(
                "Heating recipe has neither result_item nor result_fluid: "
                        + recipe
        );
    }
}