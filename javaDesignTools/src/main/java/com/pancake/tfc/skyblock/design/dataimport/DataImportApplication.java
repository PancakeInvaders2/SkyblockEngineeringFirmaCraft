package com.pancake.tfc.skyblock.design.dataimport;

import com.pancake.tfc.skyblock.design.dataimport.services.DataImporter;
import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessImporter;
import com.pancake.tfc.skyblock.design.dataimport.services.process.loottable.LootTableImporter;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.inspector.CountAndExample;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.inspector.GameDataInspector;
import com.pancake.tfc.skyblock.design.dataimport.services.GameDataLoader;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.RecipeImporter;
import com.pancake.tfc.skyblock.design.dataimport.services.resources.ResourceImporter;
import com.pancake.tfc.skyblock.design.dataimport.services.scenario.ScenarioResourceAssigner;
import com.pancake.tfc.skyblock.design.dataimport.services.technology.TechnologyProcessLinker;
import com.pancake.tfc.skyblock.design.persistence.entities.Process;
import com.pancake.tfc.skyblock.design.persistence.entities.Resource;
import com.pancake.tfc.skyblock.design.persistence.entities.Technology;
import com.pancake.tfc.skyblock.design.persistence.repositories.ProcessRepository;
import com.pancake.tfc.skyblock.design.persistence.repositories.ResourceRepository;
import com.pancake.tfc.skyblock.design.persistence.repositories.ScenarioResourceRepository;
import com.pancake.tfc.skyblock.design.persistence.repositories.TechnologyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

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
    private final ResourceImporter resourceImporter;
    private final ProcessImporter processImporter;
    private final TechnologyProcessLinker technologyProcessLinker;
    private final DataImporter dataImporter;
    private final LootTableImporter lootTableImporter;



    public DataImportApplication(
            GameDataLoader service,
            GameDataInspector inspector,
            RecipeImporter recipeImporter,
            ResourceImporter resourceImporter,
            ProcessImporter processImporter,
            TechnologyProcessLinker technologyProcessLinker,
            LootTableImporter lootTableImporter,
            DataImporter dataImporter) {
        this.service = service;
        this.inspector = inspector;
        this.recipeImporter = recipeImporter;
        this.resourceImporter = resourceImporter;
        this.processImporter = processImporter;
        this.technologyProcessLinker = technologyProcessLinker;
        this.lootTableImporter = lootTableImporter;
        this.dataImporter = dataImporter;

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

        LOG.info("gameData : {} item tags", gameData.itemTags().size());
        LOG.info("gameData : {} fluid tags", gameData.fluidTags().size());
        LOG.info("gameData : {} loot tables", gameData.lootTables().size());
        LOG.info("gameData : {} recipes", gameData.recipes().size());

        Map<String, Integer> recipeCountPerTypeBeforeParsing = recipeImporter.recipeCountPerTypeBeforeParsing(gameData);

        Map<String, CountAndExample> recipeCountsAndExamplesPerType = inspector.inspectRecipeTypes(gameData);

        Map<String, List<ParsedProcess>> parsedProcessesPerType = recipeImporter.importRecipes(gameData);

        parsedProcessesPerType.putAll(lootTableImporter.importLootTables(gameData));

        for(Map.Entry<String, List<ParsedProcess>> entry : parsedProcessesPerType.entrySet()){

            Integer recipeCountBeforeParsing = recipeCountPerTypeBeforeParsing.get(entry.getKey());
            if(recipeCountBeforeParsing == null){
                recipeCountBeforeParsing = 0;
            }
            LOG.info("{}/{} parsed processes of type {}", entry.getValue().size(), recipeCountBeforeParsing, entry.getKey());
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
                , "custom:clicking_pot_with_bowl"
                , "tfc:barrel_sealed"
                , "tfc:scraping"
                , "minecraft:blasting"
                , "tfc:blast_furnace"
                , "tfc:barrel_instant_fluid"
                , "tfc:knapping"
                , "tfc:loom"
                , "tfc:quern"
                , "tfc:bloomery"
                , "minecraft:campfire_cooking"
                , "tfc:chisel"
                , "tfc:pot"
                , "minecraft:smoking"
                , "custom:clicking_raw_rock_with_hammer"
                , "entity_loot_table"

        );

        for(Map.Entry<String, List<ParsedProcess>> entry : parsedProcessesPerType.entrySet()){
            if(!noPrint.contains(entry.getKey())){
                int i = 0;
                for(ParsedProcess parsedProcess: entry.getValue()){
                    LOG.info("{} | parsedProcess: {}", entry.getKey(), parsedProcess);
                    i++;
                    if(i >= 10){
                        break;
                    }

                }
            }
        }


        List<ParsedProcess> parsedProcesses = new ArrayList<>();
        for(Map.Entry<String, List<ParsedProcess>> entry : parsedProcessesPerType.entrySet() ){
            parsedProcesses.addAll(entry.getValue());
        }

        LOG.info("#################################");

        Map<String, Resource> resources = resourceImporter.importResources(parsedProcesses);

        LOG.info("resource count: {}", resources.size());

        LOG.info("#################################");

        List<Process> processes = processImporter.importProcesses(parsedProcesses, resources);

        Map<String, List<Process>> processesByType = processes.stream()
                .collect(groupingBy(Process::getType));

        LOG.info("Process count: {}", processes.size());
        LOG.info("Process types: {}", processesByType.keySet());

        LOG.info("#################################");

        Map<String, Technology> technologies = technologyProcessLinker.linkProcessesToTechnology(processesByType, gameData, resources);
        LOG.info("Technology count: {}", technologies.size());
        for(Map.Entry<String, Technology> technology : technologies.entrySet()){
            LOG.info(technology);
        }

        dataImporter.importData(resources, processes, technologies);


        long durationNanos = System.nanoTime() - startNanos;

        LOG.debug(
                "Data parsed and persisted in {} ms",
                durationNanos / 1_000_000.0
        );
    }


}