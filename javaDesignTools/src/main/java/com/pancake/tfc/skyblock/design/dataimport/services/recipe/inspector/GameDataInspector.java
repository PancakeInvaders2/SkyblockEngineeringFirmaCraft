package com.pancake.tfc.skyblock.design.dataimport.services.recipe.inspector;


import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameDataInspector {

    private static final Logger LOG =
            LogManager.getLogger(GameDataInspector.class);

    public void inspect(GameData gameData) {
        inspectRecipeTypes(gameData);
        inspectTopLevelFields(gameData);
        inspectRecipeStructures(gameData);

        inspectRecipeExamples(gameData);
    }

    public Map<String, CountAndExample> inspectRecipeTypes(GameData gameData) {
        Map<String, CountAndExample> countsAndExamplesByType = new HashMap<>();



        for(Map.Entry<String, JsonNode> entry : gameData.recipes().entrySet()){
            String type = entry.getValue().path("type").asString();

            CountAndExample countAndExample = countsAndExamplesByType.get(type);
            if(countAndExample == null){
                countAndExample = new CountAndExample(0, null);
            }


            countAndExample = countAndExample.increment();
            if(countAndExample.example() == null){
                countAndExample = countAndExample.withExample(entry.getValue());
            }

            countsAndExamplesByType.put(type, countAndExample);
        }

        return countsAndExamplesByType;
    }

    private void inspectRecipeExamples(GameData gameData) {
        Map<String, Map.Entry<String, JsonNode>> examples = new LinkedHashMap<>();

        gameData.recipes().forEach((recipeId, recipe) -> {
            String type = recipe.path("type").asString();
            examples.putIfAbsent(type, Map.entry(recipeId, recipe));
        });

        LOG.info("\nRecipe examples:");

        examples.forEach((type, entry) -> {
            System.out.println();
            LOG.info("========================================");
            LOG.info(type);
            LOG.info("recipe id: {}", entry.getKey());
            LOG.info("----------------------------------------");
            LOG.info(entry.getValue().toPrettyString());
        });
    }

    private void inspectTopLevelFields(GameData gameData) {
        Map<String, Long> counts = gameData.recipes().values().stream()
                .flatMap(json -> {
                    TreeSet<String> fields = new TreeSet<>();

                    json.properties().forEach(property ->
                            fields.add(property.getKey())
                    );

                    return fields.stream();
                })
                .collect(Collectors.groupingBy(
                        field -> field,
                        TreeMap::new,
                        Collectors.counting()
                ));

        LOG.info("=== Recipe top-level fields ===");

        counts.forEach((field, count) ->
                LOG.info("  {} : {}", field, count)
        );
    }

    private void inspectRecipeStructures(GameData gameData) {
        Map<String, Map<String, Long>> countsByType =
                new TreeMap<>();

        for (JsonNode recipe : gameData.recipes().values()) {
            String type = recipeType(recipe);

            TreeSet<String> fields = new TreeSet<>();

            recipe.properties().forEach(property ->
                    fields.add(property.getKey())
            );

            String signature = String.join(", ", fields);

            countsByType
                    .computeIfAbsent(type, ignored -> new TreeMap<>())
                    .merge(signature, 1L, Long::sum);
        }

        LOG.info("=== Recipe structures by type ===");

        countsByType.forEach((type, signatures) -> {
            LOG.info("  {}", type);

            signatures.forEach((signature, count) ->
                    LOG.info("    [{}] : {}", signature, count)
            );
        });
    }

    private String recipeType(JsonNode recipe) {
        JsonNode type = recipe.get("type");

        if (type == null || !type.isString()) {
            return "<missing>";
        }

        return type.stringValue();
    }
}