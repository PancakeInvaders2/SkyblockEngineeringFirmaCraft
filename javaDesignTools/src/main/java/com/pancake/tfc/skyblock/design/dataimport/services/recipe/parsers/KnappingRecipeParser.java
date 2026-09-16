package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.TagUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class KnappingRecipeParser implements RecipeParser {

    private static final String TYPE = "tfc:knapping";

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
        JsonNode result = recipe.get("result");

        if (result == null
                || !result.isObject()
                || !result.get("id").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported knapping result: " + result
            );
        }

        String resultId = result.get("id").asString();
        JsonNode ingredient = recipe.get("ingredient");

        if (ingredient != null) {
            Set<String> inputAlternatives = parseIngredient(
                    ingredient,
                    gameData
            );

            return new ParsedProcess(
                    recipeId,
                    recipeId,
                    TYPE,
                    List.of(inputAlternatives),
                    Set.of(resultId)
            );
        }

        JsonNode knappingType = recipe.get("knapping_type");

        if (knappingType == null || !knappingType.isString()) {
            throw new IllegalArgumentException(
                    "Missing knapping_type: " + recipe
            );
        }

        Set<String> implicitIngredient = getImplicitIngredient(
                knappingType.asString(),
                gameData
        );

        if (implicitIngredient == null) {
            return null;
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                TYPE,
                List.of(implicitIngredient),
                Set.of(resultId)
        );
    }
    private Set<String> getImplicitIngredient(
            String knappingType,
            GameData gameData
    ) {
        return switch (knappingType) {
            case "tfc:clay" ->
                    TagUtils.getItemTagResourceIds(
                            "tfc:clay_knapping",
                            gameData
                    );

            case "tfc:fire_clay" ->
                    TagUtils.getItemTagResourceIds(
                            "tfc:fire_clay_knapping",
                            gameData
                    );

            case "tfc:leather" ->
                    TagUtils.getItemTagResourceIds(
                            "c:leathers",
                            gameData
                    );

            case "tfc:goat_horn" -> null;

            default -> throw new IllegalArgumentException(
                    "Unsupported knapping type without ingredient: "
                            + knappingType
            );
        };
    }

    private Set<String> parseIngredient(
            JsonNode ingredient,
            GameData gameData
    ) {
        if (ingredient.isObject()) {
            JsonNode item = ingredient.get("item");

            if (item != null && item.isString()) {
                return Set.of(item.asString());
            }

            JsonNode tag = ingredient.get("tag");

            if (tag != null && tag.isString()) {
                return new HashSet<>(
                        TagUtils.getItemTagResourceIds(tag.asString(), gameData)
                );
            }
        }

        throw new IllegalArgumentException(
                "Unsupported knapping ingredient: " + ingredient
        );
    }
}