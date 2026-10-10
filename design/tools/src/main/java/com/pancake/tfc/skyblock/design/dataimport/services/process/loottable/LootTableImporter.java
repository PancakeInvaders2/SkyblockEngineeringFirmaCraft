package com.pancake.tfc.skyblock.design.dataimport.services.process.loottable;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LootTableImporter {

    private static final Logger LOG =
            LogManager.getLogger(LootTableImporter.class);


    private final EntityLootTableParser entityLootTableParser;
    private final DepositLootTableParser depositLootTableParser;

    private final List<String> IGNORED_ENTITIES = List.of(
            "minecraft:entities/sniffer"
    );

    public LootTableImporter(EntityLootTableParser entityLootTableParser,
                             DepositLootTableParser depositLootTableParser){
        this.entityLootTableParser = entityLootTableParser;
        this.depositLootTableParser = depositLootTableParser;
    }

    public Map<String, List<ParsedProcess>> importLootTables(GameData gameData) {

        Map<String, List<ParsedProcess>> parsedProcessesPerType = new HashMap<>();
        List<ParsedProcess> parsedEntityLootTables = importEntityLootTables(gameData);
        parsedProcessesPerType.put(ProcessType.ENTITY_LOOT_TABLE.processType, parsedEntityLootTables);

        List<ParsedProcess> parsedPanningLootTables = importDepositLootTables(gameData);
        parsedProcessesPerType.put(ProcessType.DEPOSIT_PANNING.processType, parsedPanningLootTables);


        return parsedProcessesPerType;
    }

    private List<ParsedProcess> importDepositLootTables(GameData gameData) {
        Map<String, JsonNode> entityLootTables = gameData.lootTables().entrySet()
                .stream()
                .filter(e -> e.getKey().split(":")[1].startsWith("deposit"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<ParsedProcess> parsedDepositLootTables = new ArrayList<>();

        for (Map.Entry<String, JsonNode> entry : entityLootTables.entrySet()) {
            String depositId = entry.getKey();
            JsonNode lootTableJson = entry.getValue();


            try {

                ParsedProcess parsedProcess = depositLootTableParser.parse(depositId, lootTableJson, gameData);


                if (parsedProcess == null
                        || parsedProcess.outputs().isEmpty()) {
                    LOG.info("/!\\ Loot tabme needs to be implemented or ignored: {}: {}", depositId, lootTableJson);
                }

                if (parsedProcess != null && !parsedProcess.outputs().isEmpty()) {

                    if (parsedProcess.inputGroups().isEmpty()
                            || parsedProcess.inputGroups().stream().anyMatch(Set::isEmpty)
                            || parsedProcess.inputGroups().stream().anyMatch(set -> set.stream().anyMatch(String::isBlank))) {
                        throw new IllegalArgumentException(
                                "loot table has missing/bad inputs: " + depositId
                        );
                    }

                    parsedDepositLootTables.add(parsedProcess);
                }


            } catch (Exception e) {
                LOG.error("Failed to parse {}", lootTableJson, e);
                throw new IllegalArgumentException(e);
            }

        }

        return parsedDepositLootTables;

    }


    private List<ParsedProcess> importEntityLootTables(GameData gameData) {
        Map<String, JsonNode> entityLootTables = gameData.lootTables().entrySet()
                .stream()
                .filter(e -> e.getKey().split(":")[1].startsWith("entities"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<ParsedProcess> parsedEntityLootTables = new ArrayList<>();

        for (Map.Entry<String, JsonNode> entry : entityLootTables.entrySet()) {
            String entityId = entry.getKey();
            JsonNode lootTableJson = entry.getValue();

            if(!IGNORED_ENTITIES.contains(entityId)) {


                try {

                    ParsedProcess parsedProcess = entityLootTableParser.parse(entityId, lootTableJson, gameData);


                    if (parsedProcess == null
                            || parsedProcess.outputs().isEmpty()) {
                        LOG.info("/!\\ Loot tabme needs to be implemented or ignored: {}: {}", entityId, lootTableJson);
                    }

                    if (parsedProcess != null && !parsedProcess.outputs().isEmpty()) {

                        if (parsedProcess.inputGroups().isEmpty()
                                || parsedProcess.inputGroups().stream().anyMatch(Set::isEmpty)
                                || parsedProcess.inputGroups().stream().anyMatch(set -> set.stream().anyMatch(String::isBlank))) {
                            throw new IllegalArgumentException(
                                    "loot table has missing/bad inputs: " + entityId
                            );
                        }

                        parsedEntityLootTables.add(parsedProcess);
                    }


                } catch (Exception e) {
                    LOG.error("Failed to parse {}", lootTableJson, e);
                    throw new IllegalArgumentException(e);
                }

            }
        }

        return parsedEntityLootTables;
    }
}
