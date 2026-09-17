package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BarrelSealedRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.BARREL_SEALED);
    }

    @Override
    public boolean supportsFluids() {
        return true;
    }

    private static final Logger LOG =
            LogManager.getLogger(BarrelSealedRecipeParser.class);


    @Override
    public ParsedProcess parse(
            String recipeId,
            JsonNode recipe,
            GameData gameData
    ) {
        List<Set<String>> inputGroups = new ArrayList<>();

        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());

        JsonNode inputFluid = recipe.get("input_fluid");
        if (inputFluid != null) {
            inputGroups.addAll(parseFluidInput(inputFluid, gameData));
        }

        JsonNode inputItem = recipe.get("input_item");
        if (inputItem != null) {
            inputGroups.addAll(parseItemInput(inputItem, gameData));
        }

        Set<String> outputs = new HashSet<>();

        JsonNode outputItem = recipe.get("output_item");
        if (outputItem != null) {
            String outputItemId = getDirectId(outputItem, "id");

            if (outputItemId != null) {
                outputs.add(outputItemId);
            }
        }

        JsonNode outputFluid = recipe.get("output_fluid");
        if (outputFluid != null) {
            String outputFluidId = getDirectId(outputFluid, "id");

            if (outputFluidId != null) {
                outputs.add(outputFluidId);
            }
        }

        /*
         * Some barrel_sealed recipes only modify the input item through
         * modifiers, for example dyeing, bleaching, pickling, brining,
         * preservation, etc.
         *
         * Those transformations don't expose a new concrete resource ID,
         * so they have no useful representation in the current reachability
         * graph.
         */
        if (outputs.isEmpty()) {
            return null;
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                inputGroups,
                outputs
        );
    }

    private List<Set<String>> parseFluidInput(
            JsonNode input,
            GameData gameData
    ) {
        List<Set<String>> groups = new ArrayList<>();

        JsonNode fluid = input.get("fluid");
        if (fluid != null && fluid.isString()) {
            groups.add(Set.of(fluid.asString()));
            return groups;
        }

        JsonNode tag = input.get("tag");
        if (tag != null && tag.isString()) {
            groups.add(TagUtils.getFluidTagResourceIds(tag.asString(), gameData));
            return groups;
        }

        throw new IllegalArgumentException(
                "Unsupported barrel_sealed input_fluid: " + input
        );
    }

    private List<Set<String>> parseItemInput(
            JsonNode input,
            GameData gameData
    ) {
        return parseItemIngredient(input, gameData);
    }

    private List<Set<String>> parseItemIngredient(
            JsonNode ingredient,
            GameData gameData
    ) {
        List<Set<String>> groups = new ArrayList<>();

        if (ingredient.isArray()) {
            /*
             * An array of ingredients means OR alternatives.
             *
             * Example:
             * [
             *   {"item": "foo"},
             *   {"tag": "bar"}
             * ]
             *
             * means one of those alternatives, not both.
             */
            Set<String> alternatives = new HashSet<>();

            for (JsonNode alternative : ingredient) {
                List<Set<String>> alternativeGroups =
                        parseItemIngredient(alternative, gameData);

                for (Set<String> group : alternativeGroups) {
                    alternatives.addAll(group);
                }
            }

            if (!alternatives.isEmpty()) {
                groups.add(alternatives);
            }

            return groups;
        }

        JsonNode item = ingredient.get("item");
        if (item != null && item.isTextual()) {
            groups.add(Set.of(item.asString()));
            return groups;
        }

        JsonNode tag = ingredient.get("tag");
        if (tag != null && tag.isTextual()) {
            Set<String> resources = gameData.itemTags().get(tag.asString());

            if (resources == null || resources.isEmpty()) {
                throw new IllegalArgumentException(
                        "Unknown or empty item tag: " + tag.asString()
                );
            }

            groups.add(new HashSet<>(resources));
            return groups;
        }

        /*
         * tfc:and is used to combine an actual ingredient with conditions
         * such as tfc:not_rotten.
         *
         * Only children that resolve to actual resources contribute groups.
         */
        JsonNode and = ingredient.get("children");
        if ("tfc:and".equals(ingredient.path("type").asString())
                && and != null
                && and.isArray()) {

            for (JsonNode child : and) {
                List<Set<String>> childGroups =
                        parseItemIngredient(child, gameData);

                groups.addAll(childGroups);
            }

            return groups;
        }

        /*
         * Some compound structures may use the type field but contain
         * children in a slightly different shape. Handle the normal
         * neoforge compound form explicitly as well.
         */
        if ("neoforge:compound".equals(ingredient.path("type").asString())) {
            JsonNode children = ingredient.get("children");

            if (children != null && children.isArray()) {
                for (JsonNode child : children) {
                    List<Set<String>> childGroups =
                            parseItemIngredient(child, gameData);

                    groups.addAll(childGroups);
                }

                return groups;
            }
        }

        /*
         * A child such as:
         *
         * {
         *   "type": "tfc:not_rotten"
         * }
         *
         * is a condition/modifier rather than a resource ingredient.
         * Ignore it.
         *
         * Likewise, unsupported modifier-only structures produce no group.
         */
        return groups;
    }

    private String getDirectId(JsonNode node, String fieldName) {
        JsonNode id = node.get(fieldName);

        if (id != null && id.isTextual()) {
            return id.asString();
        }

        return null;
    }
}