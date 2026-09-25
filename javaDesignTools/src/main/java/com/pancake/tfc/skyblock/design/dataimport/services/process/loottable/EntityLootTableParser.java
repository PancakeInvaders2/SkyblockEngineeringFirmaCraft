package com.pancake.tfc.skyblock.design.dataimport.services.process.loottable;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessSubtype;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.*;

@Component
public class EntityLootTableParser {

    public ParsedProcess parse(String entityId, JsonNode lootTable, GameData gameData){

        ProcessType processType = ProcessType.ENTITY_LOOT_TABLE;
        ParsedProcess processInConstruction = new ParsedProcess()
                .withType(processType)
                .withId("loot_"+entityId
                        .replace(":", "_")
                        .replace("/", "_")
                )
                .withName("Loot "+entityId);

        processInConstruction.inputGroups().add(Set.of(entityId));

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
            throw new IllegalArgumentException("Could not parse loot table for entity " + entityId + " : " + lootTable , e);
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
        if(false){
            // placeholder
        }
        else {
            throw new IllegalArgumentException("Unknown bool data at path " + path);
        }
        return processInConstruction;
    }

    private ParsedProcess handleNumber(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, int value) {
        if ("/pools/entries/weight".equals(path)){
            // ignored
        }
        else if ("/pools/entries/quality".equals(path)){
            // ignored
        }
        else {
            throw new IllegalArgumentException("Unknown number data at path " + path);
        }
        return processInConstruction;
    }

    private ParsedProcess handleString(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, String value) {
        if("/lootType".equals(path)){
            // ignored, we only handle loot Type ENTITY in this class
        }
        else if ("/pools/name".equals(path)){
            // ignored, the pool name doesn't much matter (ex: "pool0")
        }
        else if ("/pools/entries/type".equals(path)){
            // ignored, entry type is handled through the item field
        }
        else if ("/pools/entries/kind".equals(path)){
            // ignored, this identifies the kind of loot entry (item) and we model everything as a resource
        }
        else if ("/pools/entries/item".equals(path)){
            processInConstruction.outputs().add(value);
        }
        else {
            throw new IllegalArgumentException("Unknown string data at path " + path);
        }
        return processInConstruction;
    }



}
