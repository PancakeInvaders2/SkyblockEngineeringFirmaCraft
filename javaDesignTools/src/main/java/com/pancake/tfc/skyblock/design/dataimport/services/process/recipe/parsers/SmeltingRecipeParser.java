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
public class SmeltingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.SMELTING);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        List<Set<String>> inputGroups = new ArrayList<>();

        inputGroups.add(parseIngredient(recipe.path("ingredient"), gameData));
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        String resultId = recipe.path("result").path("id").asString();

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                inputGroups,
                Set.of(resultId)
        );
    }
    private Set<String> parseIngredient(JsonNode ingredient, GameData gameData) {
        if (ingredient.isArray()) {
            Set<String> items = new HashSet<>();

            for (JsonNode alternative : ingredient) {
                items.addAll(parseIngredient(alternative, gameData));
            }

            return items;
        }

        if (ingredient.has("item")) {
            return Set.of(ingredient.path("item").asString());
        }

        if (ingredient.has("tag")) {
            return Set.copyOf(
                    gameData.itemTags()
                            .getOrDefault(ingredient.path("tag").asString(), Set.of())
            );
        }

        throw new IllegalArgumentException(
                "Unsupported smelting ingredient: " + ingredient
        );
    }
}