package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class RecipeImporter {

    private static final Logger LOG =
            LogManager.getLogger(RecipeImporter.class);

    private static final Set<String> igneousIntrusiveStoneTypes = Set.of("granite", "diorite", "gabbro");
    private static final Set<String> igneousExtrusiveStoneTypes = Set.of("rhyolite", "dacite", "andesite", "basalt");
    private static final Set<String> igneousStoneTypes = union(igneousIntrusiveStoneTypes, igneousExtrusiveStoneTypes);
    private static final Set<String> metamorphicStoneTypes = Set.of("marble", "slate", "quartzite", "phyllite", "schist", "gneiss");
    private static final Set<String> sedimentaryStoneTypes = Set.of( "claystone", "tuff", "shale", "conglomerate", "chalk", "chert", "limestone", "dolomite");
    private static final Set<String> stoneTypes = union(igneousStoneTypes, metamorphicStoneTypes, sedimentaryStoneTypes);


    private static final Set<String> IGNORED_RECIPE_TYPES = Set.of(
            "minecraft:crafting_special_firework_star",
            "minecraft:crafting_special_mapextending",
            "minecraft:crafting_special_tippedarrow",
            "minecraft:crafting_special_firework_star_fade",
            "minecraft:crafting_special_shulkerboxcoloring",
            "minecraft:crafting_special_shielddecoration",
            "minecraft:crafting_special_armordye",
            "minecraft:crafting_special_firework_rocket",
            "minecraft:crafting_special_bannerduplicate",
            "minecraft:crafting_special_repairitem",
            "minecraft:crafting_special_bookcloning",
            "minecraft:crafting_special_mapcloning",
            "minecraft:crafting_decorated_pot",
            "minecraft:smithing_transform",
            "minecraft:campfire_cooking", // the minecraft campfire is not and will not be reachable
            "minecraft:smoking", // the minecraft smoker is not and will not be reachable
            "tfc:casting_crafting",
            "tfc:food_combining",
            "tfc:landslide",
            "tfc:sewing", // armor trims, no inputs specified
            "tfc:pot_jam", // making jam isn't a necessary part of the tech tree, just noise
            "minecraft:stonecutting", // vanilla decoration
            "minecraft:smithing_trim"

    );

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

            "tfc:knapping/feel_goat_horn_goat_horn",
            "tfc:knapping/sing_goat_horn_goat_horn",
            "tfc:knapping/admire_goat_horn_goat_horn",
            "tfc:knapping/ponder_goat_horn_goat_horn",
            "tfc:knapping/yearn_goat_horn_goat_horn",
            "tfc:knapping/seek_goat_horn_goat_horn",
            "tfc:knapping/dream_goat_horn_goat_horn",
            "tfc:knapping/call_goat_horn_goat_horn",

            "minecraft:cooked_salmon_from_smoking",
            "minecraft:cooked_cod_from_smoking",
            "minecraft:cooked_rabbit_from_smoking",
            "minecraft:cooked_mutton_from_smoking",
            "minecraft:cooked_beef_from_smoking",
            "minecraft:cooked_chicken_from_smoking",
            "minecraft:baked_potato_from_smoking",
            "minecraft:cooked_porkchop_from_smoking"
            );

    private static final List<Pattern> IGNORED_RECIPE_REGEX = List.of(
            Pattern.compile("^tfc:pot/jam_.*_canning_[1-5]$")
    );

    private final List<RecipeParser> parsers;
    private final RcpParser recipeParser;

    public RecipeImporter(List<RecipeParser> parsers, RcpParser recipeParser) {
        this.parsers = parsers;
        this.recipeParser = recipeParser;

    }

    public Map<String, List<ParsedProcess>> importRecipes(GameData gameData) {

        Map<String, List<ParsedProcess>> parsedProcessesPerType = new HashMap<>();

        Map<String, Integer> recipeCountPerTypeBeforeParsing = recipeCountPerTypeBeforeParsing(gameData);

        for (Map.Entry<String, JsonNode>entry : gameData.recipes().entrySet()) {
            String recipeId = entry.getKey();
            JsonNode recipeJson = entry.getValue();

            String recipeType = recipeJson.get("type").asString();

            if (!isIgnoredRecipe(recipeId, recipeType)) {

                List<ParsedProcess> processesOfThisType = parsedProcessesPerType.get(recipeType);
                if (processesOfThisType == null) {
                    processesOfThisType = new ArrayList<>();
                    parsedProcessesPerType.put(recipeType, processesOfThisType);
                }
                try {


                    ParsedProcess parsedProcess =  recipeParser.parse(recipeId, recipeJson, gameData);
                    // Some recipes do not expose a concrete resource output.
                    // They may represent destruction or state/NBT/trait modification
                    // rather than a resource transformation, and it's not critical to model them
                    // ex: {"type":"tfc:heating","ingredient":{"tag":"c:foods/bread"},"temperature":700}

                    if (parsedProcess == null
                            || parsedProcess.outputs().isEmpty()) {
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


                } catch (Exception e) {
                    LOG.error("Failed to parse {}", recipeJson, e);
                    throw new IllegalArgumentException(e);
                }

            }
        }

        // custom processes for processes implied but not present in the json data:
        parsedProcessesPerType.put(ProcessType.CLICKING_POT_WITH_BOWL.processType, List.of(new ParsedProcess(
                "custom:clicking_pot_with_bowl",
                "custom:clicking_pot_with_bowl",
                ProcessType.CLICKING_POT_WITH_BOWL,
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

        List<ParsedProcess> stoneAnvilCreationProcesses = new ArrayList<>();
        parsedProcessesPerType.put(ProcessType.CLICKING_RAW_ROCK_WITH_HAMMER.processType, stoneAnvilCreationProcesses);
        for(String igneousStoneType : igneousStoneTypes){
            String id = "custom:clicking_raw_"+igneousStoneType+"_with_hammer";
            stoneAnvilCreationProcesses.add(new ParsedProcess(
                    id,
                    id,
                    ProcessType.CLICKING_RAW_ROCK_WITH_HAMMER,
                    List.of(Set.of("tfc:rock/raw/" + igneousStoneType), TagUtils.getItemTagResourceIds("c:tools/hammer", gameData)),
                    Set.of(	"tfc:rock/anvil/"+igneousStoneType)
            ));
        }

                // TODO the ignored recipe tfc:crafting/flower_cutting allows duplication
        //     of flowers if you can get a cutting from them with shears.
        //     This is irrelevant to basic reachability because it doesn't create
        //     a new resource, but it matters for determining whether flowers are
        //     renewable/infinite.
        //     This is hard to model alongside world-generated finite resources
        //     such as trees, so revisit when implementing resource renewability.


        return parsedProcessesPerType;
    }

    public Map<String, Integer> recipeCountPerTypeBeforeParsing(GameData gameData) {

        Map<String, Integer> recipeCountPerType = new HashMap<>();
        for(Map.Entry<String, JsonNode> entry: gameData.recipes().entrySet()){
            String recipeType = entry.getValue().get("type").asString();

            Integer count = recipeCountPerType.get(recipeType);
            if(count == null){
                count = 0;
            }
            recipeCountPerType.put(recipeType, count+1);
        }
        return recipeCountPerType;

    }

    private static boolean isIgnoredRecipe(String recipeId, String recipeType) {
        return IGNORED_RECIPE_IDS.contains(recipeId)
                || IGNORED_RECIPE_REGEX.stream()
                .anyMatch(pattern -> pattern.matcher(recipeId).matches())
                || IGNORED_RECIPE_TYPES.contains(recipeType);
    }


    private static <T> Set<T> union( Set<T>... setArray){
        Set<T> result = new HashSet<>();
        for(Set<T> set : setArray){
            result.addAll(set);
        }
        return result;
    }
}