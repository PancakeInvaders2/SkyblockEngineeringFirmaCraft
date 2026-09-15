package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class GlassworkingRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:glassworking";

    @Override
    public boolean supports(String recipeType) {
        return RECIPE_TYPE.equals(recipeType);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode batch = recipe.get("batch");

        if (batch == null) {
            throw new IllegalArgumentException(
                    "Glassworking recipe has no batch: " + recipe
            );
        }

        List<Set<String>> inputGroups = parseBatch(batch, gameData);

        JsonNode result = recipe.get("result");
        JsonNode resultId = result == null ? null : result.get("id");

        if (resultId == null || !resultId.isString()) {
            throw new IllegalArgumentException(
                    "Glassworking recipe has no result id: " + recipe
            );
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                inputGroups,
                Set.of(resultId.asString())
        );
    }

    private List<Set<String>> parseBatch(
            JsonNode batch,
            GameData gameData
    ) {
        if (batch.has("item")) {
            return List.of(Set.of(
                    batch.get("item").asString()
            ));
        }

        if (batch.has("tag")) {
            String tag = batch.get("tag").asString();

            List<String> items = gameData.itemTags().get(tag);

            if (items == null) {
                throw new IllegalArgumentException(
                        "Unknown item tag: " + tag
                );
            }

            return List.of(Set.copyOf(items));
        }

        throw new IllegalArgumentException(
                "Unsupported glassworking batch: " + batch
        );
    }
}
