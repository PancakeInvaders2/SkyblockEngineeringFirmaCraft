package com.pancake.tfc.skyblock.design.dataimport.services.recipe.parsers;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PotSoupRecipeParser implements RecipeParser {

    private static final String RECIPE_TYPE = "tfc:pot_soup";
    private static final String OUTPUT = "custom:soup_in_pot";

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
        List<Set<String>> inputGroups = new ArrayList<>();

        JsonNode fluidIngredient = recipe.get("fluid_ingredient");

        if (fluidIngredient == null
                || !fluidIngredient.has("fluid")) {
            throw new IllegalArgumentException(
                    "Soup recipe has no fluid ingredient: " + recipe
            );
        }

        inputGroups.add(Set.of(
                fluidIngredient.get("fluid").asString()
        ));

        for (JsonNode ingredient : recipe.get("ingredients")) {
            Set<String> resources = new HashSet<>();

            for (JsonNode child : ingredient.path("children")) {
                if (child.has("item")) {
                    resources.add(child.get("item").asString());
                } else if (child.has("tag")) {
                    String tag = child.get("tag").asString();

                    Set<String> items = gameData.itemTags().get(tag);

                    if (items == null) {
                        throw new IllegalArgumentException(
                                "Unknown item tag: " + tag
                        );
                    }

                    resources.addAll(items);
                }
            }

            inputGroups.add(resources);
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                RECIPE_TYPE,
                inputGroups,
                Set.of(OUTPUT)
        );
    }
}
