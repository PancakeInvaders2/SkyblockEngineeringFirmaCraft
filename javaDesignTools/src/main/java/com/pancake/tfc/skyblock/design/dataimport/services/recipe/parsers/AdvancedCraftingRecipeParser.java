package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;


@Component
public class AdvancedCraftingRecipeParser implements RecipeParser {

    private static final String SHAPED = "tfc:advanced_shaped_crafting";
    private static final String SHAPELESS = "tfc:advanced_shapeless_crafting";

    @Override
    public boolean supports(String recipeType) {
        return SHAPED.equals(recipeType) || SHAPELESS.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData) {
        String type = recipe.get("type").asString();

        List<Set<String>> inputGroups = switch (type) {
            case SHAPED -> parseShapedInputs(recipe, gameData);
            case SHAPELESS -> parseShapelessInputs(recipe, gameData);
            default -> throw new IllegalArgumentException(
                    "Unsupported advanced crafting recipe type: " + type);
        };

        Set<String> outputs = parseResult(recipe);

        return new ParsedProcess(
                recipeId,
                recipeId,
                type,
                inputGroups,
                outputs
        );
    }

    private List<Set<String>> parseShapelessInputs(
            JsonNode recipe,
            GameData gameData
    ) {
        List<Set<String>> inputGroups = new ArrayList<>();

        for (JsonNode ingredient : recipe.get("ingredients")) {
            inputGroups.addAll(
                    parseIngredientGroups(ingredient, gameData)
            );
        }

        return inputGroups;
    }

    private List<Set<String>> parseShapedInputs(JsonNode recipe, GameData gameData) {
        List<Set<String>> inputGroups = new ArrayList<>();

        JsonNode key = recipe.get("key");
        JsonNode pattern = recipe.get("pattern");

        Set<String> usedSymbols = new HashSet<>();

        for (JsonNode row : pattern) {
            for (int i = 0; i < row.asString().length(); i++) {
                char symbol = row.asString().charAt(i);

                if (symbol != ' ') {
                    usedSymbols.add(String.valueOf(symbol));
                }
            }
        }

        for (String symbol : usedSymbols) {
            JsonNode ingredient = key.get(symbol);

            if (ingredient == null) {
                throw new IllegalArgumentException(
                        "Recipe " + recipe + " uses symbol '" + symbol
                                + "' but it has no key entry");
            }

            inputGroups.addAll(
                    parseIngredientGroups(ingredient, gameData)
            );

        }

        return inputGroups;
    }

    private Set<String> parseIngredient(
            JsonNode ingredient,
            GameData gameData
    ) {
        if (ingredient.has("item")) {
            return Set.of(ingredient.get("item").asString());
        }

        if (ingredient.has("tag")) {
            String tag = ingredient.get("tag").asString();

            List<String> items = gameData.itemTags().get(tag);

            if (items == null) {
                throw new IllegalArgumentException(
                        "Unknown item tag: " + tag
                );
            }

            return Set.copyOf(items);
        }

        return Set.of();

    }

    private List<Set<String>> parseIngredientGroups(
            JsonNode ingredient,
            GameData gameData
    ) {
        if ("tfc:and".equals(ingredient.path("type").asString())) {
            List<Set<String>> groups = new ArrayList<>();

            for (JsonNode child : ingredient.get("children")) {
                groups.addAll(parseIngredientGroups(child, gameData));
            }

            return groups;
        }

        Set<String> alternatives = parseIngredient(ingredient, gameData);

        return alternatives.isEmpty()
                ? List.of()
                : List.of(alternatives);
    }

    private Set<String> parseResult(JsonNode recipe) {
        JsonNode result = recipe.get("result");

        if (result.has("stack")) {
            result = result.get("stack");
        }

        Set<String> outputs = new HashSet<>();

        if (result.has("id")) {
            outputs.add(result.get("id").asString());
        }

        return outputs;
    }

}