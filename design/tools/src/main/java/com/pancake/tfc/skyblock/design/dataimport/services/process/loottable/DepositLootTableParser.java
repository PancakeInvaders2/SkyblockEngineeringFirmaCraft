package com.pancake.tfc.skyblock.design.dataimport.services.process.loottable;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Set;

@Component
public class DepositLootTableParser {

    public ParsedProcess parse(String depositId, JsonNode lootTable, GameData gameData){

        ProcessType processType = ProcessType.DEPOSIT_PANNING;
        ParsedProcess processInConstruction = new ParsedProcess()
                .withType(processType)
                .withId("panning_"+depositId
                        .replace(":", "_")
                        .replace("/", "_")
                )
                .withName("Panning "+depositId);

        processInConstruction.inputGroups().add(Set.of(depositId));

        try{

            processInConstruction = handleObject( gameData, processInConstruction, processType, "", lootTable.asObject());


            if(StringUtils.isBlank(processInConstruction.id()) ){
                throw new IllegalArgumentException("no process id");
            }
            if(StringUtils.isBlank(processInConstruction.name()) ){
                throw new IllegalArgumentException("no process name");
            }
            if(processInConstruction.type() == null ){
                throw new IllegalArgumentException("no process type");
            }

        }
        catch (Exception e){
            throw new IllegalArgumentException("Could not parse loot table for entity " + depositId + " : " + lootTable , e);
        }
        return processInConstruction;

    }

    private ParsedProcess handleChild(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String childPath, JsonNode childNode) {

        return switch(childNode.getNodeType()){
            case OBJECT -> handleObject(gameData, processInConstruction, processType, childPath, childNode.asObject());
            case ARRAY -> handleArray( gameData, processInConstruction, processType, childPath, childNode.asArray());
            case BOOLEAN -> handleBool( gameData, processInConstruction, processType, childPath, childNode.asBoolean());
            case NUMBER -> handleNumber( gameData, processInConstruction, processType, childPath, childNode.asInt());
            case STRING -> handleString( gameData, processInConstruction, processType, childPath, childNode.asString());
            case POJO -> throw new IllegalArgumentException("POJO child");
            case MISSING -> throw new IllegalArgumentException("MISSING child");
            case NULL -> throw new IllegalArgumentException("NULL child");
            case BINARY -> throw new IllegalArgumentException("BINARY child");
        };

    }

    private ParsedProcess handleObject(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, ObjectNode object){

        for( String propertyName : object.propertyNames()){
            String childPath = path + "/" + propertyName;
            JsonNode childNode = object.get(propertyName);

            processInConstruction = handleChild(gameData, processInConstruction, processType, childPath, childNode);
        }
        return processInConstruction;
    }

    private ParsedProcess handleArray(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, ArrayNode array) {

        for (int i = 0; i < array.size(); i++){
            JsonNode childNode = array.get(i);
            // the children don't need to know their index compared to their siblings, just their path from the root
            processInConstruction = handleChild(gameData, processInConstruction, processType, path, childNode);

        }

        return processInConstruction;
    }

    private ParsedProcess handleBool(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, boolean value) {
        if("/placeholder".equals(path)){
            // TODO placeholder
        }
        else {
            throw new IllegalArgumentException("Unknown bool data at path " + path);
        }
        return processInConstruction;
    }

    private ParsedProcess handleNumber(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, int value) {
        if ("/pools/entries/entries/weight".equals(path)){
            // ignored
        }
        else if ("/pools/entries/entries/quality".equals(path)){
            // ignored
        }
        else {
            throw new IllegalArgumentException("Unknown number data at path " + path);
        }
        return processInConstruction;
    }

    private ParsedProcess handleString(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, String value) {
        if("/lootType".equals(path)){
            // ignored
        }
        else if ("/pools/name".equals(path)){
            // ignored
        }
        else if ("/pools/entries/type".equals(path)){
            // ignored
        }
        else if ("/pools/entries/kind".equals(path)){
            // ignored
        }
        else if ("/pools/entries/entries/type".equals(path)){
            // ignored
        }
        else if ("/pools/entries/entries/kind".equals(path)){
            // ignored
        }
        else if ("/pools/entries/entries/item".equals(path)){
            processInConstruction.outputs().add(value);
        }
        else {
            throw new IllegalArgumentException("Unknown string data at path " + path);
        }
        return processInConstruction;
    }



}
