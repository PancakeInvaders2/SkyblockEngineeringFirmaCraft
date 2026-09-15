package com.pancake.tfc.skyblock.design.dataimport;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.inspector.CountAndExample;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.inspector.GameDataInspector;
import com.pancake.tfc.skyblock.design.dataimport.services.GameDataLoader;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.recipe.RecipeImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
@EntityScan(basePackages = "com.pancake.tfc.skyblock.design.persistence.entities")
@EnableJpaRepositories(
        basePackages = "com.pancake.tfc.skyblock.design.persistence.repositories"
)
public class DataImportApplication
        implements CommandLineRunner {

    private static final Logger LOG =
            LogManager.getLogger(DataImportApplication.class);

    private final GameDataLoader service;
    private final GameDataInspector inspector;
    private final RecipeImporter recipeImporter;

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
            "minecraft:crafting_decorated_pot",
            "tfc:sewing",
            "tfc:food_combining"
    );

    public DataImportApplication(
            GameDataLoader service,
            GameDataInspector inspector,
            RecipeImporter recipeImporter) {
        this.service = service;
        this.inspector = inspector;
        this.recipeImporter = recipeImporter;
    }

    public static void main(String[] args) {

        SpringApplication.run(
                DataImportApplication.class,
                args
        );

    }

    @Override
    public void run(String... args) throws IOException, URISyntaxException {
        long startNanos = System.nanoTime();

        GameData gameData = service.load();

        LOG.info("gameData : {} tags", gameData.itemTags().size());
        LOG.info("gameData : {} loot tables", gameData.lootTables().size());
        LOG.info("gameData : {} recipes", gameData.recipes().size());


        Map<String, CountAndExample> recipeCountsAndExamplesPerType = inspector.inspectRecipeTypes(gameData);

        Map<String, List<ParsedProcess>> parsedProcessesPerType = recipeImporter.importRecipes(gameData);

        for(Map.Entry<String, List<ParsedProcess>> entry : parsedProcessesPerType.entrySet()){
            LOG.info("{} parsed processes of type {}", entry.getValue().size(), entry.getKey());
        }

        List<String> noPrint = List.of(
                "tfc:advanced_shapeless_crafting"
                , "tfc:advanced_shaped_crafting"
                , "minecraft:crafting_shaped"
                , "minecraft:crafting_shapeless"
                , "tfc:heating"
                , "tfc:casting"
                , "tfc:anvil"
                , "tfc:alloy"
                , "tfc:pot_soup"
                , "tfc:welding"
                , "tfc:barrel_instant"
                , "tfc:collapse"
                , "minecraft:smelting"
                , "tfc:glassworking"
        );

        for(Map.Entry<String, List<ParsedProcess>> entry : parsedProcessesPerType.entrySet()){
            if(!noPrint.contains(entry.getKey())){
                int i = 0;
                for(ParsedProcess parsedProcess: entry.getValue()){
                    LOG.info("{} | parsedProcess: {}", entry.getKey(), parsedProcess);
                    i++;
                    if(i >= 3){
                        break;
                    }

                }
            }
        }

        Optional<Map.Entry<String, CountAndExample>> nextCandidate = recipeCountsAndExamplesPerType
                .entrySet()
                .stream()
                .filter(e -> !parsedProcessesPerType.containsKey(e.getKey()))
                .filter(e -> !IGNORED_RECIPE_TYPES.contains(e.getKey()))
                .findAny();

        LOG.info("Next candidate:");
        if(nextCandidate.isPresent()){
            LOG.info("type: {}", nextCandidate.get().getKey());
            LOG.info("count: {}", nextCandidate.get().getValue().count());
            LOG.info("example: {}", nextCandidate.get().getValue().example());
        }
        else {
            LOG.info("none !");
        }

        long durationNanos = System.nanoTime() - startNanos;

        LOG.debug(
                "Data parsed in {} ms",
                durationNanos / 1_000_000.0
        );
    }
}