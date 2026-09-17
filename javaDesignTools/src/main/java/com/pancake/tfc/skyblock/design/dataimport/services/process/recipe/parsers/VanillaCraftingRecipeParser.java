package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessSubtype;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VanillaCraftingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.CRAFTING_SHAPED, ProcessType.CRAFTING_SHAPELESS);
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
        String type = recipe.get("type").asString();

        ProcessType processType = ProcessType.fromRecipeType(type);

        List<Set<String>> inputGroups = switch (processType) {
            case CRAFTING_SHAPED -> parseShapedInputs(recipe, gameData);
            case CRAFTING_SHAPELESS -> parseShapelessInputs(recipe, gameData);
            default -> throw new IllegalArgumentException(
                    "Unsupported vanilla crafting recipe type: " + type);
        };

        ProcessSubtype subtype = switch (processType) {
            case CRAFTING_SHAPED -> parseShapedSubtype(recipe);
            case CRAFTING_SHAPELESS -> parseShapelessSubtype(recipe);
            default -> throw new IllegalArgumentException(
                    "Unsupported vanilla crafting recipe type: " + type);
        };

        Set<String> outputs = parseResult(recipe);

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                Optional.of(subtype),
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

    private ProcessSubtype parseShapedSubtype(JsonNode recipe) {
        JsonNode pattern = recipe.get("pattern");

        int rows = pattern.size();
        int columns = 0;

        for (JsonNode row : pattern) {
            columns = Math.max(columns, row.asString().length());
        }

        if (rows <= 2 && columns <= 2) {
            return ProcessSubtype.CRAFTING_2x2;
        }

        if (rows <= 3 && columns <= 3) {
            return ProcessSubtype.CRAFTING_3x3;
        }

        throw new IllegalArgumentException(
                "Unexpected vanilla crafting grid size: "
                        + rows + "x" + columns
                        + " in recipe: " + recipe
        );
    }

    private ProcessSubtype parseShapelessSubtype(JsonNode recipe) {
        int ingredientCount = recipe.get("ingredients").size();

        if (ingredientCount <= 4) {
            return ProcessSubtype.CRAFTING_2x2;
        }

        return ProcessSubtype.CRAFTING_3x3;
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

            Set<String> items = gameData.itemTags().get(tag);

            if (items == null) {
                throw new IllegalArgumentException(
                        "Unknown item tag: " + tag
                );
            }

            return List.of(Set.copyOf(items));
        }


        if ("tfc:fluid_content".equals(ingredient.path("type").asString())) {
            JsonNode fluid = ingredient.get("fluid");

            if (fluid == null || !fluid.has("fluid")) {
                throw new IllegalArgumentException(
                        "Malformed tfc:fluid_content ingredient: "
                                + ingredient
                );
            }

            String fluidId = fluid.get("fluid").asString();

            return List.of(Set.of(fluidId));
        }

        if ("tfc:and".equals(ingredient.path("type").asString())) {
            List<Set<String>> groups = new ArrayList<>();

            for (JsonNode child : ingredient.path("children")) {
                groups.addAll(parseIngredientGroups(child, gameData));
            }

            return groups;
        }

        if ("tfc:not_rotten".equals(ingredient.path("type").asString())) {
            return List.of();
        }

        if ("neoforge:difference".equals(ingredient.path("type").asString())) {
            JsonNode base = ingredient.get("base");
            JsonNode subtracted = ingredient.get("subtracted");

            Set<String> baseResources =
                    parseIngredientGroups(base, gameData).getFirst();

            Set<String> subtractedResources =
                    parseIngredientGroups(subtracted, gameData).getFirst();

            Set<String> resources = new HashSet<>(baseResources);
            resources.removeAll(subtractedResources);

            return List.of(resources);
        }

        throw new IllegalArgumentException(
                "Unsupported vanilla crafting ingredient: " + ingredient
        );
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