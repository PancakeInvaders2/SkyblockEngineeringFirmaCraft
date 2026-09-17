package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.TagUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class QuernRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.QUERN);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode ingredient = recipe.get("ingredient");
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());


        if (ingredient == null) {
            throw new IllegalArgumentException(
                    "Missing quern ingredient: " + recipe
            );
        }

        Set<String> inputAlternatives = parseIngredient(
                ingredient,
                gameData
        );

        JsonNode result = recipe.get("result");

        if (result == null
                || !result.isObject()) {
            throw new IllegalArgumentException(
                    "Unsupported quern result: " + result
            );
        }

        JsonNode resultId = result.get("id");

        if (resultId == null || !resultId.isString()) {
            JsonNode stack = result.get("stack");

            if (stack == null
                    || !stack.isObject()
                    || !stack.get("id").isString()) {
                throw new IllegalArgumentException(
                        "Unsupported quern result: " + result
                );
            }

            resultId = stack.get("id");
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(inputAlternatives),
                Set.of(resultId.asString())
        );
    }

    private Set<String> parseIngredient(
            JsonNode ingredient,
            GameData gameData
    ) {
        if (ingredient.isArray()) {
            Set<String> alternatives = new HashSet<>();

            for (JsonNode alternative : ingredient) {
                alternatives.addAll(
                        parseIngredient(alternative, gameData)
                );
            }

            return alternatives;
        }

        if (ingredient.isObject()) {
            JsonNode item = ingredient.get("item");

            if (item != null && item.isString()) {
                return Set.of(item.asString());
            }

            JsonNode tag = ingredient.get("tag");

            if (tag != null && tag.isString()) {
                return new HashSet<>(
                        TagUtils.getItemTagResourceIds(
                                tag.asString(),
                                gameData
                        )
                );
            }

            JsonNode type = ingredient.get("type");

            if (type != null
                    && type.isString()
                    && "tfc:and".equals(type.asString())) {

                Set<String> alternatives = new HashSet<>();

                for (JsonNode child : ingredient.get("children")) {
                    alternatives.addAll(
                            parseIngredientChild(child, gameData)
                    );
                }

                return alternatives;
            }
        }

        throw new IllegalArgumentException(
                "Unsupported quern ingredient: " + ingredient
        );
    }

    private Set<String> parseIngredientChild(
            JsonNode child,
            GameData gameData
    ) {
        JsonNode item = child.get("item");

        if (item != null && item.isString()) {
            return Set.of(item.asString());
        }

        JsonNode tag = child.get("tag");

        if (tag != null && tag.isString()) {
            return new HashSet<>(
                    TagUtils.getItemTagResourceIds(
                            tag.asString(),
                            gameData
                    )
            );
        }

        // Conditions such as tfc:not_rotten do not contribute
        // resources to the reachability graph.
        return Set.of();
    }
}