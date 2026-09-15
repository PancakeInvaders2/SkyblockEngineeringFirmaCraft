package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class VanillaCraftingRecipeParser implements RecipeParser {

    private static final String SHAPED = "minecraft:crafting_shaped";
    private static final String SHAPELESS = "minecraft:crafting_shapeless";

    @Override
    public boolean supports(String recipeType) {
        return SHAPED.equals(recipeType) || SHAPELESS.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        String type = recipe.get("type").asString();

        List<Set<String>> inputGroups = switch (type) {
            case SHAPED -> parseShapedInputs(recipe, gameData);
            case SHAPELESS -> parseShapelessInputs(recipe, gameData);
            default -> throw new IllegalArgumentException(
                    "Unsupported vanilla crafting recipe type: " + type);
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
            inputGroups.addAll(parseIngredientGroups(ingredient, gameData));
        }

        return inputGroups;
    }

    private List<Set<String>> parseShapedInputs(
            JsonNode recipe,
            GameData gameData
    ) {
        List<Set<String>> inputGroups = new ArrayList<>();

        JsonNode key = recipe.get("key");
        JsonNode pattern = recipe.get("pattern");

        Set<String> usedSymbols = new HashSet<>();

        for (JsonNode row : pattern) {
            String rowString = row.asString();

            for (int i = 0; i < rowString.length(); i++) {
                char symbol = rowString.charAt(i);

                if (symbol != ' ') {
                    usedSymbols.add(String.valueOf(symbol));
                }
            }
        }

        for (String symbol : usedSymbols) {
            JsonNode ingredient = key.get(symbol);

            if (ingredient == null) {
                throw new IllegalArgumentException(
                        "Recipe " + recipe
                                + " uses symbol '" + symbol + "' but it has no key entry"
                );
            }

            inputGroups.addAll(parseIngredientGroups(ingredient, gameData));
        }

        return inputGroups;
    }
    private List<Set<String>> parseIngredientGroups(
            JsonNode ingredient,
            GameData gameData
    ) {
        if (ingredient.isArray()) {
            Set<String> alternatives = new HashSet<>();

            for (JsonNode alternative : ingredient) {
                List<Set<String>> groups =
                        parseIngredientGroups(alternative, gameData);

                for (Set<String> group : groups) {
                    alternatives.addAll(group);
                }
            }

            return List.of(alternatives);
        }

        if (ingredient.has("item")) {
            return List.of(Set.of(
                    ingredient.get("item").asString()
            ));
        }

        if (ingredient.has("tag")) {
            String tag = ingredient.get("tag").asString();

            List<String> items = gameData.itemTags().get(tag);

            if (items == null) {
                throw new IllegalArgumentException(
                        "Unknown item tag: " + tag
                );
            }

            return List.of(Set.copyOf(items));
        }

        if ("tfc:and".equals(ingredient.path("type").asString())) {
            List<Set<String>> groups = new ArrayList<>();

            for (JsonNode child : ingredient.path("children")) {
                groups.addAll(parseIngredientGroups(child, gameData));
            }

            return groups;
        }

        return List.of();
    }

    private Set<String> parseResult(JsonNode recipe) {
        JsonNode result = recipe.get("result");

        Set<String> outputs = new HashSet<>();

        if (result.has("id")) {
            outputs.add(result.get("id").asString());
        }

        return outputs;
    }
}