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
public class BarrelInstantFluidRecipeParser implements RecipeParser {

    @Override
    public List<ProcessType> getSupportedRecipeTypes() {
        return List.of(ProcessType.BARREL_INSANT_FLUID);
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
        JsonNode primaryFluid = recipe.get("primary_fluid");

        ProcessType processType = ProcessType.fromRecipeType(recipe.get("type").asString());


        if (primaryFluid == null
                || !primaryFluid.isObject()
                || !primaryFluid.get("fluid").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported barrel_instant_fluid primary fluid: " + primaryFluid
            );
        }

        JsonNode addedFluid = recipe.get("added_fluid");

        if (addedFluid == null
                || !addedFluid.isObject()
                || !addedFluid.get("fluid").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported barrel_instant_fluid added fluid: " + addedFluid
            );
        }

        JsonNode outputFluid = recipe.get("output_fluid");

        if (outputFluid == null
                || !outputFluid.isObject()
                || !outputFluid.get("id").isString()) {
            throw new IllegalArgumentException(
                    "Unsupported barrel_instant_fluid output fluid: " + outputFluid
            );
        }

        return new ParsedProcess(
                recipeId,
                recipeId,
                processType,
                List.of(
                        Set.of(primaryFluid.get("fluid").asString()),
                        Set.of(addedFluid.get("fluid").asString())
                ),
                Set.of(outputFluid.get("id").asString())
        );
    }
}