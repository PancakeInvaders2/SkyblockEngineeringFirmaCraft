package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

@Component
public class GlassworkingRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.GLASSWORKING);
    }

    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        JsonNode batch = recipe.get("batch");
        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());


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
                processType,
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

            Set<String> items = gameData.itemTags().get(tag);

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
