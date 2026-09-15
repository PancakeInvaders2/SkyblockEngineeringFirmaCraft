package com.pancake.tfc.skyblock.design.dataimport.services.recipe;

import com.pancake.tfc.skyblock.design.dataimport.DataImportApplication;
import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.*;

@Service
public class RecipeImporter {

    private static final Logger LOG =
            LogManager.getLogger(RecipeImporter.class);

    public static final List<String> IGNORED_RECIPE_IDS = List.of(
            "tfc:barrel/water_cooling",
            "tfc:barrel/olive_oil_cooling",
            "tfc:barrel/salt_water_cooling",
            "tfc:barrel/canola_oil_cooling",
            "tfc:heating/burn_bread",
            "tfc:heating/burn_meat",
            "tfc:crafting/add_large_bait",
            "tfc:crafting/add_small_bait",
            "tfc:crafting/salting",
            "tfc:crafting/flower_cutting",
            "tfc:crafting/add_powder",
            "tfc:barrel/clean_bowl",
            "tfc:barrel/yellow_dyeable",
            "tfc:barrel/gray_dyeable",
            "tfc:barrel/magenta_dyeable",
            "tfc:barrel/black_dyeable",
            "tfc:barrel/green_dyeable",
            "tfc:barrel/light_gray_dyeable",
            "tfc:barrel/cyan_dyeable",
            "tfc:barrel/lime_dyeable",
            "tfc:barrel/blue_dyeable",
            "tfc:barrel/white_dyeable",
            "tfc:barrel/light_blue_dyeable",
            "tfc:barrel/orange_dyeable",
            "tfc:barrel/pink_dyeable",
            "tfc:barrel/brown_dyeable",
            "tfc:barrel/bleach_dyeable",
            "tfc:barrel/red_dyeable",
            "tfc:barrel/purple_dyeable",
            "tfc:barrel/brined",
            "tfc:barrel/preserved_in_vinegar",
            "tfc:barrel/pickled",

            "tfc:barrel/vinegar",

            "tfc:barrel/tannin",

            "tfc:barrel/cider",
            "tfc:barrel/vodka:",
            "tfc:barrel/rye_whiskey",
            "tfc:barrel/corn_whiskey",
            "tfc:barrel/rum",
            "tfc:barrel/whiskey",
            "tfc:barrel/sake",
            "tfc:barrel/curdled_milk",
            "tfc:barrel/beer"



            );

    private final List<RecipeParser> parsers;

    public RecipeImporter(List<RecipeParser> parsers) {
        this.parsers = parsers;
    }

    public Map<String, List<ParsedProcess>> importRecipes(GameData gameData) {

        Map<String, List<ParsedProcess>> parsedProcessesPerType = new HashMap<>();

        for (var entry : gameData.recipes().entrySet()) {
            String recipeId = entry.getKey();
            JsonNode recipeJson = entry.getValue();

            String recipeType = recipeJson.get("type").asString();

            Optional<RecipeParser> parser = parsers.stream()
                    .filter(candidate -> candidate.supports(recipeType))
                    .findFirst()
                    // TODO uncomment when all recipe parsers are implemented
                    //.orElseThrow(() ->
                    //        new IllegalArgumentException(
                    //                "No recipe parser for recipe: "
                    //                        + recipeId
                    //        )
                    //)
                    ;

            if(parser.isPresent()){

                List<ParsedProcess> processesOfThisType = parsedProcessesPerType.get(recipeType);
                if( processesOfThisType == null){
                    processesOfThisType = new ArrayList<>();
                    parsedProcessesPerType.put(recipeType, processesOfThisType);
                }
                try{

                    ParsedProcess parsedProcess = parser.get().parse(recipeId, recipeJson, gameData);
                    // Some recipes do not expose a concrete resource output.
                    // They may represent destruction or state/NBT/trait modification
                    // rather than a resource transformation, and it's not critical to model them
                    // ex: {"type":"tfc:heating","ingredient":{"tag":"c:foods/bread"},"temperature":700}

                    if ( !IGNORED_RECIPE_IDS.contains(recipeId) && (
                            parsedProcess == null
                            || parsedProcess.outputs().isEmpty() )) {
                        LOG.info("/!\\ Recipe needs to be implemented or ignored: {}: {}", recipeId, recipeJson);
                    }

                    if (parsedProcess != null && !parsedProcess.outputs().isEmpty()) {

                        if (parsedProcess.inputGroups().isEmpty()
                                || parsedProcess.inputGroups().stream().anyMatch(Set::isEmpty)
                                || parsedProcess.inputGroups().stream().anyMatch(set -> set.stream().anyMatch(String::isBlank))) {
                            throw new IllegalArgumentException(
                                    "Recipe has missing/bad inputs: " + recipeId
                            );
                        }

                        processesOfThisType.add(parsedProcess);
                    }
                }
                catch(Exception e){
                    LOG.error("Failed to parse {}", recipeJson, e);
                    throw new IllegalArgumentException(e);
                }
            }

        }

        // custom processes for processes implied in the json data:
        parsedProcessesPerType.put("custom:clicking_pot_with_bowl", List.of(new ParsedProcess(
                "custom:clicking_pot_with_bowl",
                "custom:clicking_pot_with_bowl",
                "custom:clicking_pot_with_bowl",
                List.of(Set.of("custom:soup_in_pot")),
                Set.of(	"minecraft:rabbit_stew",
                        "minecraft:suspicious_stew",
                        "minecraft:mushroom_stew",
                        "tfc:food/vegetables_soup",
                        "tfc:food/dairy_soup",
                        "minecraft:beetroot_soup",
                        "tfc:food/grain_soup",
                        "tfc:food/fruit_soup",
                        "tfc:food/protein_soup"
                )
        )));

        // TODO the ignored recipe tfc:crafting/flower_cutting allows duplication
        //     of flowers if you can get a cutting from them with shears.
        //     This is irrelevant to basic reachability because it doesn't create
        //     a new resource, but it matters for determining whether flowers are
        //     renewable/infinite.
        //     This is hard to model alongside world-generated finite resources
        //     such as trees, so revisit when implementing resource renewability.

        // TODO the blowpipe and glass blowing pathway uses a lot of NBT, a manual process will
        //     more appropriate than trying to coerce the json into giving us what we need



        return parsedProcessesPerType;
    }
}