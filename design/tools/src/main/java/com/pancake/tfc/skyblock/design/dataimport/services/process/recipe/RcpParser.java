package com.pancake.tfc.skyblock.design.dataimport.services.process.recipe;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessSubtype;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeType;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Component
public class RcpParser {



    public ParsedProcess parse(String recipeId, JsonNode recipe, GameData gameData){

        String type = recipe.get("type").asString();
        ProcessType processType = ProcessType.fromRecipeType(type);
        ParsedProcess processInConstruction = new ParsedProcess()
                .withType(processType)
                .withId(recipeId)
                .withName(recipeId);

        try{

            processInConstruction = handleObject( gameData, processInConstruction, processType, "", recipe.asObject());

            if( ProcessType.KNAPPING.equals( processInConstruction.type() ) ){
                if(ProcessSubtype.KNAPPING_TFC_CLAY.equals(processInConstruction.subtype().get())){
                    processInConstruction.inputGroups().add(
                        TagUtils.getItemTagResourceIds(
                                "tfc:clay_knapping",
                                gameData ));
                }
                else if(ProcessSubtype.KNAPPING_TFC_ROCK.equals(processInConstruction.subtype().get())){
                    // already covered, no need to add it again
                }
                else if(ProcessSubtype.KNAPPING_TFC_FIRE_CLAY.equals(processInConstruction.subtype().get())){
                    processInConstruction.inputGroups().add(
                            TagUtils.getItemTagResourceIds(
                                    "tfc:fire_clay_knapping",
                                    gameData ) );
                }
                else if(ProcessSubtype.KNAPPING_TFC_LEATHER.equals(processInConstruction.subtype().get())){
                    processInConstruction.inputGroups().add(
                            TagUtils.getItemTagResourceIds(
                                    "c:leathers",
                                    gameData ) );
                }
                else {
                    throw new IllegalArgumentException("Unknown knapping subtype: " + processInConstruction.subtype());
                }

            }
            else if( ProcessType.BLAST_FURNACE.equals( processInConstruction.type() ) ){
                // the tuyere loses durability and fuel is consumed
                processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds("tfc:blast_furnace_tuyeres", gameData));
                processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds("tfc:blast_furnace_fuel", gameData));

            }
            else if( ProcessType.QUERN.equals( processInConstruction.type() ) ){
                // handstone loses durability
                processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds("tfc:quern_handstones", gameData));
            }
            else if( ProcessType.POT.equals( processInConstruction.type()) ){
                // fuel
                processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds("minecraft:logs", gameData));
            }
            else if (ProcessType.POT_SOUP.equals( processInConstruction.type() ) ){
                // fuel
                processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds("minecraft:logs", gameData));
                // output not present in the json file
                processInConstruction.outputs().add("custom:soup_in_pot");
            }
            else if( ProcessType.ALLOY.equals( processInConstruction.type() ) ){
                // wood and straw for constructing the pit kiln
                processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds("minecraft:logs", gameData));
                processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds("tfc:pit_kiln_straw", gameData));
            }


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
            throw new IllegalArgumentException("Could not parse recipe " + recipe, e);
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

        if ("/ingredient".equals(path)) {
            // example: "ingredient":{"type":"tfc:and","children":[{"item":"tfc:food/red_piranha"},{"type":"tfc:not_rotten"}]}
            return handleIngredientObject(
                    gameData,
                    processInConstruction,
                    processType,
                    path,
                    object
            );
        }
        else if ("/ingredients".equals(path)) {
            return handleIngredientObject(
                    gameData,
                    processInConstruction,
                    processType,
                    path,
                    object
            );
        }
        else if ("/input_item".equals(path)) {
            return handleIngredientObject(
                    gameData,
                    processInConstruction,
                    processType,
                    path,
                    object
            );
        }
        else if ("/catalyst".equals(path)){
            return handleCatalystIngredient(
                    gameData,
                    processInConstruction,
                    processType,
                    path,
                    object
            );
        }
        else if (path.startsWith("/key/")) {
            // example!:
            // "key":{
            //  "B":{
            //   "type":"tfc:and",
            //   "children":[
            //    {"item":"tfc:food/rice_bread"},
            //    {"type":"tfc:not_rotten"}
            //   ]
            //  }
            //  ...
            // }
            return handleIngredientObject(
                    gameData,
                    processInConstruction,
                    processType,
                    path,
                    object
            );
        }

        for( String propertyName : object.propertyNames()){
            String childPath = path + "/" + propertyName;
            JsonNode childNode = object.get(propertyName);

            processInConstruction = handleChild(gameData, processInConstruction, processType, childPath, childNode);
        }
        return processInConstruction;
    }



    private ParsedProcess handleArray(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, ArrayNode array) {
        if("/pattern".equals(path) && ( ProcessType.CRAFTING_SHAPED.equals(processType) || ProcessType.ADVANCED_SHAPED_CRAFTING.equals(processType) )){
            // example: "pattern":["###","###"]
            boolean needs3x3Crafting=false;
            if(array.size() > 2){
                needs3x3Crafting = true;
            }
            else {
                for (int i = 0; i < array.size(); i++){
                    String child = array.get(i).asString();
                    if(child.length() > 2){
                        needs3x3Crafting = true;
                    }
                }
            }
            processInConstruction = processInConstruction.withSubtype( Optional.of(needs3x3Crafting ? ProcessSubtype.CRAFTING_3x3 : ProcessSubtype.CRAFTING_2x2) );

        }
        else if("/pattern".equals(path) && ProcessType.KNAPPING.equals(processType)){
            // ignored
        }
        else if ("/ingredients".equals(path)
                && (ProcessType.CRAFTING_SHAPELESS.equals(processType)
                || ProcessType.ADVANCED_SHAPELESS_CRAFTING.equals(processType))) {

            boolean needs3x3Crafting = array.size() > 4;

            processInConstruction = processInConstruction.withSubtype(
                    Optional.of(
                            needs3x3Crafting
                                    ? ProcessSubtype.CRAFTING_3x3
                                    : ProcessSubtype.CRAFTING_2x2
                    )
            );

            // Continue parsing the actual ingredients.
            for (JsonNode childNode : array) {
                processInConstruction = handleChild(
                        gameData,
                        processInConstruction,
                        processType,
                        path,
                        childNode
                );
            }
        }
        else {
            for (int i = 0; i < array.size(); i++){
                JsonNode childNode = array.get(i);
                // the children don't need to know their index compared to their siblings, just their path from the root
                processInConstruction = handleChild(gameData, processInConstruction, processType, path, childNode);

            }
        }
        return processInConstruction;
    }



    private ParsedProcess handleBool(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, boolean value) {
        if("/use_durability".equals(path)){
            // ignored, we'll asume durability is used for all inputs
        }
        else if("/default_on".equals(path)){
            // ignored
        }
        else if("/apply_bonus".equals(path)){
            // ignored
        }
        else {
            throw new IllegalArgumentException("Unknown bool data at path " + path);
        }
        return processInConstruction;
    }

    private ParsedProcess handleNumber(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, int value) {
        if("/result/count".equals(path)){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/result_fluid/amount".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/result_item/stack/count".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/result/stack/count".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/item_output/count".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/output_item/count".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/result_item/count".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/input_fluid/amount".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/result/amount".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/fluid_ingredient/amount".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/input_item/count".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/contents/max".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/contents/min".equals(path)) {
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/break_chance".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/fluid/amount".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/ingredient/count".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/ingredients/fluid/amount".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/output_fluid/amount".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/added_fluid/amount".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/fluid_output/amount".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/remainder/modifiers/stack/count".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/primary_ingredient/fluid/amount".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ( "/primary_fluid/amount".equals(path) ){
            // nothing to do, we don't model quantities, just reachability
        }
        else if ("/temperature".equals(path)) {
            ProcessSubtype subtype = ProcessSubtype.valueOf(processType.name() + "_" + value);
            processInConstruction = processInConstruction.withSubtype(Optional.of(subtype));
        }
        else if ("/tier".equals(path)) {
            ProcessSubtype subtype = ProcessSubtype.valueOf(processType.name() + "_" + ("" + value).replace("-", "MINUS"));
            processInConstruction = processInConstruction.withSubtype(Optional.of(subtype));
        }
        else if ( "/result/modifiers/food/decay_modifier".equals(path) ){
            // ignored
        }
        else if ( "/result/modifiers/food/hunger".equals(path) ){
            // ignored
        }
        else if ( "/result/modifiers/food/saturation".equals(path) ){
            // ignored
        }
        else if ( "/result/modifiers/food/water".equals(path) ){
            // ignored
        }
        else if ( "/result/modifiers/portions/nutrient_modifier".equals(path) ){
            // ignored
        }
        else if ( "/result/modifiers/portions/saturation_modifier".equals(path) ){
            // ignored
        }
        else if ( "/result/modifiers/portions/water_modifier".equals(path) ){
            // ignored
        }
        else if ( "/input_column".equals(path) ){
            // ignored
        }
        else if ( "/input_row".equals(path) ){
            // ignored
        }
        else if ( "/duration".equals(path) ){
            // ignored
        }
        else if ( "/cookingtime".equals(path) ){
            // ignored
        }
        else if ( "/experience".equals(path) ){
            // ignored
        }
        else if ( "/steps".equals(path) ){
            // ignored
        }
        else if ( "/result_item/modifiers/chance".equals(path) ){
            // ignored
        }
        else {
            throw new IllegalArgumentException("Unknown number data at path " + path);
        }
        return processInConstruction;
    }

    private ParsedProcess handleString(GameData gameData, ParsedProcess processInConstruction, ProcessType processType, String path, String value) {
        if("/type".equals(path)){
            // nothing to do, already handled
        }
        else if ("/category".equals(path)) {
            // Known but intentionally ignored
        }
        else if ("/input_fluid/fluid".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/fluid/fluid".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/input_item/item".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/ingredients/item".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/ingredient".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/ingredient/item".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/first_input/item".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/second_input/item".equals(path)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/contents/fluid".equals(path)) {
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/ingredients/fluid/fluid".equals(path)) {
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/added_fluid/fluid".equals(path)) {
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/fluid_ingredient/fluid".equals(path)) {
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/primary_fluid/fluid".equals(path)) {
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/mold/item".equals(path)) {
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/batch/item".equals(path) && ProcessType.GLASSWORKING.equals(processType)) {
            // not a tag, just an item id
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if ("/batch/tag".equals(path) && ProcessType.GLASSWORKING.equals(processType)) {
            processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds(value,gameData));
        }
        else if ("/operations".equals(path) && ProcessType.GLASSWORKING.equals(processType)) {
            // ignored
        }
        else if (path.startsWith("/key/") && path.endsWith("/item")) {
            processInConstruction.inputGroups().add(Set.of(value));
        }
        else if (path.startsWith("/key/") && path.endsWith("/tag")) {
            processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds(value,gameData));
        }
        else if ("/input_fluid/tag".equals(path)){
            processInConstruction.inputGroups().add(TagUtils.getFluidTagResourceIds(value,gameData));
        }
        else if ("/ingredients/tag".equals(path)){
            processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds(value,gameData));
        }
        else if ("/ingredient/tag".equals(path)){
            processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds(value,gameData));
        }
        else if ("/first_input/tag".equals(path)){
            processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds(value,gameData));
        }
        else if ("/second_input/tag".equals(path)){
            processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds(value,gameData));
        }
        else if ("/input_item/tag".equals(path)){
            processInConstruction.inputGroups().add(TagUtils.getItemTagResourceIds(value,gameData));
        }
        else if ("/primary_ingredient/tag".equals(path)) {
            // Known but intentionally ignored.
            // The primary ingredient is already represented in /ingredients.
        }
        else if ("/primary_ingredient/item".equals(path)) {
            // Known but intentionally ignored.
            // The primary ingredient is already represented in /ingredients.
        }
        else if ("/primary_ingredient/fluid/fluid".equals(path)) {
            // Known but intentionally ignored.
            // The primary ingredient is already represented in /ingredients.
        }
        else if ("/primary_ingredient/type".equals(path)) {
            // ignored
        }
        else if ("/result/id".equals(path)) {
            // just an item id
            processInConstruction.outputs().add(value);
        }
        else if ("/result_item/stack/id".equals(path)) {
            // just an item id
            processInConstruction.outputs().add(value);
        }
        else if ("/item_output/id".equals(path)) {
            // just an item id
            processInConstruction.outputs().add(value);
        }
        else if ("/output_item/id".equals(path)) {
            // just an item id
            processInConstruction.outputs().add(value);
        }
        else if ("/result/stack/id".equals(path)) {
            // just an item id
            processInConstruction.outputs().add(value);
        }
        else if ("/result_fluid/id".equals(path)) {
            // just a fluid id
            processInConstruction.outputs().add(value);
        }
        else if ("/fluid_output/id".equals(path)) {
            // just a fluid id
            processInConstruction.outputs().add(value);
        }
        else if ("/output_fluid/id".equals(path)) {
            // just a fluid id
            processInConstruction.outputs().add(value);
        }
        else if ("/result_item/id".equals(path)) {
            // just a fluid id
            processInConstruction.outputs().add(value);
        }
        else if ("/result".equals(path)) {
            // just an item id
            processInConstruction.outputs().add(value);
        }
        else if ("/mode".equals(path)) {
            // ignored
        }
        else if ("/group".equals(path)) {
            // ignored
        }
        else if ("/remainder/modifiers/type".equals(path)) {
            // ignored
        }
        else if ("/remainder/modifiers/stack/id".equals(path)) {
            // ignored
        }
        else if ("/ingredients/type".equals(path)) {
            // ignored
        }
        else if ("/result/modifiers/type".equals(path)) {
            // ignored
        }
        else if ("/result_item/modifiers/type".equals(path)) {
            // ignored
        }
        else if ("/rules".equals(path)) {
            // ignored
        }
        else if ("/bonus".equals(path)) {
            // ignored
        }
        else if ("/result/modifiers/portions/ingredient/item".equals(path)) {
            // ignored
        }
        else if ("/result/modifiers/portions/nutrient_modifier".equals(path)) {
            // ignored
        }
        else if ("/output_item/modifiers/type".equals(path)) {
            // ignored
        }
        else if ("/output_item/modifiers/color".equals(path)) {
            // ignored
        }
        else if ("/texture".equals(path)) {
            // ignored
        }
        else if ("/input_texture".equals(path)) {
            // ignored
        }
        else if ("/output_texture".equals(path)) {
            // ignored
        }

        else if ("/knapping_type".equals(path)) {
            String subTypeString = processType + "_" + value.toUpperCase(Locale.ROOT).replace(":", "_");
            ProcessSubtype subType = ProcessSubtype.valueOf(subTypeString);
            processInConstruction = processInConstruction.withSubtype(Optional.of(subType));
        }
        else {
            throw new IllegalArgumentException("Unknown string data at path " + path);
        }
        return processInConstruction;
    }


    private ParsedProcess handleCatalystIngredient(
            GameData gameData,
            ParsedProcess processInConstruction,
            ProcessType processType,
            String path,
            ObjectNode object) {

        // catalysts are ingredients. They are consumed at least for tfc bloomeries
        Set<String> catalystIngredient = null;

        for (String propertyName : object.propertyNames()) {
            JsonNode child = object.get(propertyName);

            if ("count".equals(propertyName)) {
                // ignored
            }
            else if ("item".equals(propertyName)) {
                if (!child.isString()) {
                    throw new IllegalArgumentException(
                            "catalyst/item must be a string"
                    );
                }

                catalystIngredient = Set.of(child.stringValue());
            }
            else {
                throw new IllegalArgumentException("Unknown property in catalyst"+ propertyName);
            }
        }

        if(catalystIngredient == null){
            throw new IllegalArgumentException("No ingredient found in catalyst");
        }

        processInConstruction.inputGroups().add(catalystIngredient);
        return processInConstruction;
    }

    private ParsedProcess handleIngredientObject(
            GameData gameData,
            ParsedProcess processInConstruction,
            ProcessType processType,
            String path,
            ObjectNode object) {

        String type = object.has("type")
                ? object.get("type").asString()
                : null;

        if ("tfc:and".equals(type)) {
            return tfcAndIngredient(gameData, processInConstruction, object);
        }
        else if ("tfc:fluid_content".equals(type)) {
            return tfcFluidContentIngredient(
                    gameData,
                    processInConstruction,
                    object
            );
        }
        else if ("neoforge:difference".equals(type)) {
            return neoforgeDifferenceIngredient(
                    gameData,
                    processInConstruction,
                    object
            );
        }
        else if ("neoforge:compound".equals(type)) {
            return neoforgeCompoundIngredient(
                    gameData,
                    processInConstruction,
                    object
            );
        }
        else {

            for( String propertyName : object.propertyNames()){
                String childPath = path + "/" + propertyName;
                JsonNode childNode = object.get(propertyName);

                processInConstruction = handleChild(gameData, processInConstruction, processType, childPath, childNode);
            }
            return processInConstruction;
        }

    }

    private ParsedProcess tfcAndIngredient(GameData gameData, ParsedProcess processInConstruction, ObjectNode object) {
        // Handle a 'tfc:and' ingredient
        ArrayNode children = object.get("children").asArray();
        String item = null;
        String tag = null;
        Set<String> ids = null;
        for( int i = 0; i < children.size(); i++){
            JsonNode child = children.get(i);
            if (child.isArray()) {
                // OR: any alternative satisfies one input group
                if(ids != null){
                    throw new IllegalArgumentException("several OR arrays in a tfc:and ingredient");
                }
                ids = parseIngredientAlternatives(gameData, child.asArray() );
            }
            else if(child.isObject()) {
                for (String propertyName : child.propertyNames()) {
                    if ("item".equals(propertyName)) {
                        item = child.get(propertyName).stringValue();
                    } else if ("tag".equals(propertyName)) {
                        tag = child.get(propertyName).stringValue();
                    } else if ("type".equals(propertyName)) {
                        // ignored
                    } else {
                        throw new IllegalArgumentException("Unknown property in a tfc:and ingredient: " + propertyName);
                    }
                }
            }
            else {
                throw new IllegalArgumentException("Not supported ingredient data type in a tfc:and ingredient");
            }
        }
        if(item != null && tag != null ){
            throw new IllegalArgumentException("Both tag and item are present");
        }
        else if(item != null && ids != null ){
            throw new IllegalArgumentException("Both ids and item are present");
        }
        else if(tag != null && ids != null ){
            throw new IllegalArgumentException("Both tag and ids are present");
        }
        else if(item != null){
            processInConstruction.inputGroups().add(Set.of(item));
        }
        else if (tag != null){
            processInConstruction.inputGroups().add( TagUtils.getItemTagResourceIds( tag, gameData) );
        }
        else if (ids != null){
            processInConstruction.inputGroups().add( ids );
        }
        else {
            throw new IllegalArgumentException("Neither tag nor item are present");
        }

        return processInConstruction;
    }

    private Set<String> parseIngredientAlternatives(
            GameData gameData,
            ArrayNode alternatives) {

        Set<String> result = new HashSet<>();

        for (JsonNode alternative : alternatives) {
            if (!alternative.isObject()) {
                throw new IllegalArgumentException(
                        "Non-object alternative in ingredient OR: " + alternative
                );
            }

            if (alternative.has("item")) {
                result.add(alternative.get("item").stringValue());
            }
            else if (alternative.has("tag")) {
                result.addAll(
                        TagUtils.getItemTagResourceIds(
                                alternative.get("tag").stringValue(),
                                gameData
                        )
                );
            }
            else {
                throw new IllegalArgumentException(
                        "Unknown ingredient alternative: " + alternative
                );
            }
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Empty ingredient OR");
        }

        return result;
    }

    private ParsedProcess neoforgeDifferenceIngredient(
            GameData gameData,
            ParsedProcess processInConstruction,
            ObjectNode object) {

        Set<String> base = null;
        Set<String> subtracted = null;

        for (String propertyName : object.propertyNames()) {
            JsonNode child = object.get(propertyName);

            if ("type".equals(propertyName)) {
                if (!child.isString() || !"neoforge:difference".equals(child.stringValue())) {
                    throw new IllegalArgumentException(
                            "Unexpected type in neoforge:difference ingredient: " + child
                    );
                }
            }
            else if ("base".equals(propertyName)) {
                if (!child.isObject()) {
                    throw new IllegalArgumentException(
                            "base in neoforge:difference ingredient must be an object"
                    );
                }

                base = parseIngredientSet(gameData, child.asObject());
            }
            else if ("subtracted".equals(propertyName)) {
                if (!child.isObject()) {
                    throw new IllegalArgumentException(
                            "subtracted in neoforge:difference ingredient must be an object"
                    );
                }

                subtracted = parseIngredientSet(gameData, child.asObject());
            }
            else {
                throw new IllegalArgumentException(
                        "Unknown property in neoforge:difference ingredient: "
                                + propertyName
                );
            }
        }

        if (base == null) {
            throw new IllegalArgumentException(
                    "Missing base in neoforge:difference ingredient"
            );
        }

        if (subtracted == null) {
            throw new IllegalArgumentException(
                    "Missing subtracted in neoforge:difference ingredient"
            );
        }

        base.removeAll(subtracted);

        if (base.isEmpty()) {
            throw new IllegalArgumentException(
                    "neoforge:difference ingredient produces an empty ingredient set"
            );
        }

        processInConstruction.inputGroups().add(base);

        return processInConstruction;
    }

    private Set<String> parseIngredientSet(
            GameData gameData,
            ObjectNode object) {

        Set<String> result = null;

        for (String propertyName : object.propertyNames()) {
            JsonNode child = object.get(propertyName);

            if ("item".equals(propertyName)) {
                if (result != null) {
                    throw new IllegalArgumentException(
                            "Multiple ingredient definitions: " + object
                    );
                }

                result = Set.of(child.stringValue());
            }
            else if ("tag".equals(propertyName)) {
                if (result != null) {
                    throw new IllegalArgumentException(
                            "Multiple ingredient definitions: " + object
                    );
                }

                result = TagUtils.getItemTagResourceIds(
                        child.stringValue(),
                        gameData
                );
            }
            else {
                throw new IllegalArgumentException(
                        "Unknown property in ingredient set: " + propertyName
                );
            }
        }

        if (result == null) {
            throw new IllegalArgumentException(
                    "Ingredient set contains neither item nor tag: " + object
            );
        }

        return new HashSet<>(result);
    }

    private ParsedProcess neoforgeCompoundIngredient(
            GameData gameData,
            ParsedProcess processInConstruction,
            ObjectNode object) {

        Set<String> ids = new HashSet<>();

        for (String propertyName : object.propertyNames()) {
            JsonNode child = object.get(propertyName);

            if ("type".equals(propertyName)) {
                if (!"neoforge:compound".equals(child.stringValue())) {
                    throw new IllegalArgumentException(
                            "Unexpected type in neoforge:compound ingredient: "
                                    + child.stringValue()
                    );
                }
            }
            else if ("children".equals(propertyName)) {
                if (!child.isArray()) {
                    throw new IllegalArgumentException(
                            "children in neoforge:compound ingredient must be an array"
                    );
                }

                for (JsonNode alternative : child) {
                    if (!alternative.isObject()) {
                        throw new IllegalArgumentException(
                                "Non-object child in neoforge:compound ingredient: "
                                        + alternative
                        );
                    }

                    Set<String> alternativeIds =
                            parseIngredientSet(gameData, alternative.asObject());

                    ids.addAll(alternativeIds);
                }
            }
            else if ("count".equals(propertyName)) {
                // Quantity is intentionally ignored; reachability only.
            }
            else {
                throw new IllegalArgumentException(
                        "Unknown property in neoforge:compound ingredient: "
                                + propertyName
                );
            }
        }

        if (ids.isEmpty()) {
            throw new IllegalArgumentException(
                    "Empty neoforge:compound ingredient"
            );
        }

        processInConstruction.inputGroups().add(ids);

        return processInConstruction;
    }

    private ParsedProcess tfcFluidContentIngredient(
            GameData gameData,
            ParsedProcess processInConstruction,
            ObjectNode object) {

        String fluid = null;

        for (String propertyName : object.propertyNames()) {
            JsonNode child = object.get(propertyName);

            if ("type".equals(propertyName)) {
                if (!"tfc:fluid_content".equals(child.stringValue())) {
                    throw new IllegalArgumentException(
                            "Unexpected type in tfc:fluid_content ingredient: "
                                    + child.stringValue()
                    );
                }
            }
            else if ("fluid".equals(propertyName)) {
                if (!child.isObject()) {
                    throw new IllegalArgumentException(
                            "fluid in tfc:fluid_content ingredient must be an object"
                    );
                }

                for (String fluidPropertyName : child.propertyNames()) {
                    JsonNode fluidChild = child.get(fluidPropertyName);

                    if ("amount".equals(fluidPropertyName)) {
                        // Quantity is intentionally ignored.
                    }
                    else if ("fluid".equals(fluidPropertyName)) {
                        if (fluid != null) {
                            throw new IllegalArgumentException(
                                    "Multiple fluid values in tfc:fluid_content ingredient"
                            );
                        }

                        fluid = fluidChild.stringValue();
                    }
                    else {
                        throw new IllegalArgumentException(
                                "Unknown property in tfc:fluid_content fluid: "
                                        + fluidPropertyName
                        );
                    }
                }
            }
            else {
                throw new IllegalArgumentException(
                        "Unknown property in tfc:fluid_content ingredient: "
                                + propertyName
                );
            }
        }

        if (fluid == null) {
            throw new IllegalArgumentException(
                    "Missing fluid in tfc:fluid_content ingredient"
            );
        }

        processInConstruction.inputGroups().add(Set.of(fluid));

        return processInConstruction;
    }


}
