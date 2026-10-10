package com.pancake.tfc.skyblock.design.dataimport.services.technology;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessSubtype;
import com.pancake.tfc.skyblock.design.dataimport.services.process.ProcessType;
import com.pancake.tfc.skyblock.design.dataimport.services.process.recipe.TagUtils;
import com.pancake.tfc.skyblock.design.persistence.entities.Process;
import com.pancake.tfc.skyblock.design.persistence.entities.Resource;
import com.pancake.tfc.skyblock.design.persistence.entities.Technology;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TechnologyProcessLinker {

    private Technology craftingTableTechnology = null;
    private Technology castingTechnology = null;
    private Technology alloyTechnology = null;

    private Technology stoneAnvilTechnology = null;
    private Technology copperAnvilTechnology = null;
    private Technology bronzeAnvilTechnology = null;
    private Technology wroughtIronAnvilTechnology = null;
    private Technology steelAnvilTechnology = null;
    private Technology blackSteelAnvilTechnology = null;
    private Technology coloredSteelAnvilTechnology = null;
    private Technology smeltingTechnology = null;
    private Technology glassworkingTechnology = null;
    private Technology scrapingTechnology = null;
    private Technology blastingTechnology = null;
    private Technology blastFurnaceTechnology = null;
    private Technology barrelTechnology = null;
    private Technology loomTechnology = null;
    private Technology quernTechnology = null;
    private Technology bloomeryTechnology = null;

    private Technology firepitTechnology = null;
    private Technology pitKilnTechnology = null;
    private Technology bellowedForgeTechnology = null;

    private Technology bowlTechnology = null;
    private Technology chiselTechnology = null;
    private Technology potTechnology = null;

    public Map<String, Technology> linkProcessesToTechnology(
            Map<String, List<Process>> processesByType,
            GameData gameData,
            Map<String, Resource> resources) {

        Map<String, Technology> technologiesById = new HashMap<>();
        for(Map.Entry<String, List<Process>> processesOfType : processesByType.entrySet()  ){

            ProcessType processType = ProcessType.fromRecipeType(processesOfType.getKey());

            for( Process process : processesOfType.getValue()){

                Technology technology = getOrCreateTechnology(processType, process.getSubType(), gameData, resources);
                if(technology == null) {
                    process.setTechnologyRequired(false);
                }
                else {
                    technology.getUnlockedProcesses().add(process);
                    technologiesById.put(technology.getId(), technology);
                }
            }
        }

        return technologiesById;
    }

    private Technology getOrCreateTechnology(ProcessType processType,
                                             Optional<ProcessSubtype> subType,
                                             GameData gameData,
                                             Map<String, Resource> resources) {
        
        return switch (processType){
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS, ADVANCED_SHAPED_CRAFTING, ADVANCED_SHAPELESS_CRAFTING -> {

                if(subType.isEmpty()){
                    throw new IllegalArgumentException("missing subtype");
                }
                if( ProcessSubtype.CRAFTING_3x3.equals(subType.get())){
                    yield getOrCreateCraftingTableTechnology(gameData, resources);
                }
                yield null;
            }
            case ANVIL, WELDING -> getOrCreateAnvilTechnology(gameData, resources, subType);
            case HEATING -> getOrCreateHeatingTechnology(gameData, resources, subType);
            case CASTING -> getOrCreateCastingTechnology(gameData, resources);
            case ALLOY -> getOrCreateAlloyTechnology(gameData, resources);
            case POT_SOUP, POT -> getOrCreatePotTechnology(gameData, resources);
            case BARREL_INSTANT, BARREL_SEALED, BARREL_INSANT_FLUID -> getOrCreateBarrelTechnology(gameData, resources);
            case COLLAPSE -> null;
            case SMELTING -> getOrCreateSmeltingTechnology(gameData, resources);
            case GLASSWORKING -> getOrCreateGlassworkingTechnology(gameData, resources);
            case SCRAPING -> getOrCreateScrapingTechnology(gameData, resources);
            case BLASTING -> getOrCreateBlastingTechnology(gameData, resources);
            case BLAST_FURNACE -> getOrCreateBlastFurnaceTechnology(gameData, resources);
            case KNAPPING -> null;
            case LOOM -> getOrCreateLoomTechnology(gameData, resources);
            case QUERN -> getOrCreateQuernTechnology(gameData, resources);
            case BLOOMERY -> getOrCreateBloomeryTechnology(gameData, resources);
            case CLICKING_POT_WITH_BOWL -> getOrCreateBowlTechnology(gameData, resources);
            case CLICKING_RAW_ROCK_WITH_HAMMER -> null;
            case CHISEL -> getOrCreateChiselTechnology(gameData, resources);
            case ENTITY_LOOT_TABLE -> null;
            case DEPOSIT_PANNING -> getOrCreatePanningTechnology(gameData, resources);
        };
        
    }

    private Technology getOrCreatePanningTechnology(GameData gameData, Map<String, Resource> resources){
        if(craftingTableTechnology == null){
            craftingTableTechnology = createTechnology(
                    "deposit_panning",
                    "Deposit panning",
                    Set.of(
                        Set.of("tfc:pan/empty")
                    ),
                    resources
            );
        }
        return craftingTableTechnology;
    }

    private Technology getOrCreateCraftingTableTechnology(GameData gameData, Map<String, Resource> resources){
        if(craftingTableTechnology == null){
            craftingTableTechnology = createTechnology(
                    "crafting_table",
                    "Crafting table",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:workbenches", gameData)
                    ),
                    resources
            );
        }
        return craftingTableTechnology;
    }
    private Technology getOrCreateCastingTechnology(GameData gameData, Map<String, Resource> resources) {
        if(castingTechnology == null){
            castingTechnology = createTechnology(
                    "casting",
                    "Casting",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:vessels", gameData)
                    ),
                    resources
            );
        }
        return castingTechnology;

    }

    private Technology getOrCreateAlloyTechnology(GameData gameData, Map<String, Resource> resources) {
        if(alloyTechnology == null){
            alloyTechnology = createTechnology(
                    "alloy",
                    "Alloy",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:vessels", gameData)
                    ),
                    resources
            );
        }
        return alloyTechnology;

    }


    private Technology getOrCreateHeatingTechnology(GameData gameData, Map<String, Resource> resources, Optional<ProcessSubtype> subTypeOpt) {

        if(subTypeOpt.isEmpty()){
            throw new IllegalArgumentException("Missing heating subtype");
        }

        ProcessSubtype subType = subTypeOpt.get();

        if(subType.getTier() == null){
            throw new IllegalArgumentException("Missing heating temperature");
        }
        if(!subType.name().startsWith("HEATING_") ){
            throw new IllegalArgumentException("Subtype " + subType + " is not an heating technology");
        }
        int temperature = subType.getTier();

        if(temperature <= 600){
            return findOrCreateFirepitTechnology(gameData, resources);
        }
        else if (temperature <= 1400) {
            return findOrCreatePitKilnTechnology(gameData, resources);
        }
        else {
            return findOrCreateBellowedForgeTechnology(gameData, resources);
        }

    }

    private Technology findOrCreateFirepitTechnology(GameData gameData, Map<String, Resource> resources) {
        if(firepitTechnology == null){
            firepitTechnology = createTechnology(
                    "firepit",
                    "Firepit",
                    Set.of(
                            TagUtils.getItemTagResourceIds("minecraft:logs", gameData)
                            , TagUtils.getItemTagResourceIds("tfc:firepit_sticks", gameData)
                    ),
                    resources
            );
        }
        return firepitTechnology;
    }

    private Technology findOrCreatePitKilnTechnology(GameData gameData, Map<String, Resource> resources) {
        if(pitKilnTechnology == null){
            pitKilnTechnology = createTechnology(
                    "pit_kiln",
                    "Pit Kiln",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:pit_kiln_logs", gameData)
                            , TagUtils.getItemTagResourceIds("tfc:pit_kiln_straw", gameData)
                            , TagUtils.getItemTagResourceIds("c:tools/igniter", gameData)
                    ),
                    resources
            );
        }
        return pitKilnTechnology;
    }

    private Technology findOrCreateBellowedForgeTechnology(GameData gameData, Map<String, Resource> resources) {
        if(bellowedForgeTechnology == null){
            bellowedForgeTechnology = createTechnology(
                    "bellowed_forge",
                    "Bellowed Forge",
                    Set.of(
                            Set.of("tfc:bellows")
                            , Set.of("minecraft:charcoal")
                            , TagUtils.getItemTagResourceIds("c:tools/igniter", gameData)
                    ),
                    resources
            );
        }
        return bellowedForgeTechnology;
    }

    private Technology getOrCreateAnvilTechnology(GameData gameData, Map<String, Resource> resources, Optional<ProcessSubtype> subTypeOpt) {

        int tier = getAnvilTier(subTypeOpt);

        if(tier <= 0){
            return getOrCreateStoneAnvilTechnology(gameData, resources);
        } else if (tier == 1) {
            return getOrCreateCopperAnvilTechnology(gameData, resources);
        } else if (tier == 2) {
            return getOrCreateBronzeAnvilTechnology(gameData, resources);
        } else if (tier == 3) {
            return getOrCreateWroughtIronAnvilTechnology(gameData, resources);
        } else if (tier == 4) {
            return getOrCreateSteelAnvilTechnology(gameData, resources);
        } else if (tier == 5) {
            return getOrCreateBlackSteelAnvilTechnology(gameData, resources);
        } else if (tier == 6) {
            return getOrCreateColoredSteelAnvilTechnology(gameData, resources);
        } else {
            throw new IllegalArgumentException("Unrecognized anvil tier: " + tier);
        }

    }

    private static int getAnvilTier(Optional<ProcessSubtype> subTypeOpt) {
        if(subTypeOpt.isEmpty()){
            // some recipes don't have a tier attached, we'll assume they can be done on any anvil
            return 0;
        }
        ProcessSubtype subType = subTypeOpt.get();
        if(!subType.name().startsWith("ANVIL_") && !subType.name().startsWith("WELDING_") ){
            throw new IllegalArgumentException("Subtype " + subType + " is not an anvil technology");
        }
        if(subType.getTier() == null){
            throw new IllegalArgumentException("Subtype " + subType + " doesn't have a tier");
        }

        return subType.getTier();
    }

    private Technology getOrCreateStoneAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(stoneAnvilTechnology == null){
            stoneAnvilTechnology = createTechnology(
                    "stone_anvil",
                    "Stone Anvil",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:anvils", gameData)
                                    .stream()
                                    .filter(id -> id.startsWith("tfc:rock/anvil/"))
                                    .collect(Collectors.toSet())
                    ),
                    resources
            );
        }
        return stoneAnvilTechnology;
    }
    private Technology getOrCreateCopperAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(copperAnvilTechnology == null){
            copperAnvilTechnology = createTechnology(
                    "copper_anvil",
                    "Copper  Anvil",
                    Set.of(
                            Set.of("tfc:metal/anvil/copper")
                    ),
                    resources
            );
        }
        return copperAnvilTechnology;
    }
    private Technology getOrCreateBronzeAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(bronzeAnvilTechnology == null){
            bronzeAnvilTechnology = createTechnology(
                    "bronze_anvil",
                    "Bronze Anvil",
                    Set.of(
                            Set.of("tfc:metal/anvil/bronze", "tfc:metal/anvil/black_bronze", "tfc:metal/anvil/bismuth_bronze")
                    ),
                    resources
            );
        }
        return bronzeAnvilTechnology;
    }
    private Technology getOrCreateWroughtIronAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(wroughtIronAnvilTechnology == null){
            wroughtIronAnvilTechnology = createTechnology(
                    "wrought_iron_anvil",
                    "Wrought Iron Anvil",
                    Set.of(
                            Set.of("tfc:metal/anvil/wrought_iron")
                    ),
                    resources
            );
        }
        return wroughtIronAnvilTechnology;
    }
    private Technology getOrCreateSteelAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(steelAnvilTechnology == null){
            steelAnvilTechnology = createTechnology(
                    "steel_anvil",
                    "Steel Anvil",
                    Set.of(
                            Set.of("tfc:metal/anvil/steel")
                    ),
                    resources
            );
        }
        return steelAnvilTechnology;
    }
    private Technology getOrCreateBlackSteelAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(blackSteelAnvilTechnology == null){
            blackSteelAnvilTechnology = createTechnology(
                    "black_steel_anvil",
                    "Black Steel Anvil",
                    Set.of(
                            Set.of("tfc:metal/anvil/black_steel")
                    ),
                    resources
            );
        }
        return blackSteelAnvilTechnology;
    }
    private Technology getOrCreateColoredSteelAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(coloredSteelAnvilTechnology == null){
            coloredSteelAnvilTechnology = createTechnology(
                    "blue_red_steel_anvil",
                    "Blue/Red steel anvil",
                    Set.of(
                            Set.of("tfc:metal/anvil/blue_steel", "tfc:metal/anvil/red_steel")
                    ),
                    resources
            );
        }
        return coloredSteelAnvilTechnology;
    }

    private Technology getOrCreateSmeltingTechnology(GameData gameData, Map<String, Resource> resources) {
        if(smeltingTechnology == null){
            smeltingTechnology = createTechnology(
                    "smelting",
                    "Smelting",
                    Set.of(
                            TagUtils.getItemTagResourceIds("c:player_workstations/furnaces", gameData)
                    ),
                    resources
            );
        }
        return smeltingTechnology;

    }

    private Technology getOrCreateGlassworkingTechnology(GameData gameData, Map<String, Resource> resources) {
        if(glassworkingTechnology == null){
            glassworkingTechnology = createTechnology(
                    "glassworking",
                    "Glassworking",
                    Set.of(
                            TagUtils.getItemTagResourceIds("c:tools/blowpipe", gameData)
                            , Set.of("tfc:paddle")
                            , Set.of("tfc:jacks")
                            , Set.of("tfc:gem_saw")
                    ),
                    resources
            );
        }
        return glassworkingTechnology;

    }

    private Technology getOrCreateScrapingTechnology(GameData gameData, Map<String, Resource> resources) {
        if(scrapingTechnology == null){
            scrapingTechnology = createTechnology(
                    "scraping",
                    "Scraping",
                    Set.of(
                            TagUtils.getItemTagResourceIds("c:tools/knife", gameData)
                            , TagUtils.getItemTagResourceIds("minecraft:logs", gameData)
                    ),
                    resources
            );
        }
        return scrapingTechnology;

    }

    private Technology getOrCreateBlastingTechnology(GameData gameData, Map<String, Resource> resources) {
        if(blastingTechnology == null){
            blastingTechnology = createTechnology(
                    "blasting",
                    "Blasting",
                    Set.of(
                            Set.of("minecraft:blast_furnace")
                    ),
                    resources
            );
        }
        return blastingTechnology;

    }

    private Technology getOrCreateBlastFurnaceTechnology(GameData gameData, Map<String, Resource> resources) {
        if(blastFurnaceTechnology == null){
            blastFurnaceTechnology = createTechnology(
                    "blast_furnace",
                    "Blast furnace",
                    Set.of(
                            Set.of("tfc:blast_furnace"),
                            Set.of("tfc:reinforced_fire_bricks"),
                            Set.of("tfc:bellows"),
                            Set.of("tfc:crucible"),
                            TagUtils.getItemTagResourceIds("c:tools/igniter", gameData)
                    ),
                    resources
            );
        }

        return blastFurnaceTechnology;

    }

    private Technology getOrCreateBarrelTechnology(GameData gameData, Map<String, Resource> resources) {
        if(barrelTechnology == null){
            barrelTechnology = createTechnology(
                    "barrel",
                    "Barrel",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:barrels", gameData)
                    ),
                    resources
            );
        }
        return barrelTechnology;

    }

    private Technology getOrCreateLoomTechnology(GameData gameData, Map<String, Resource> resources) {
        if(loomTechnology == null){
            loomTechnology = createTechnology(
                    "loom",
                    "Loom",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:looms", gameData)
                    ),
                    resources
            );

        }
        return loomTechnology;

    }

    private Technology getOrCreateQuernTechnology(GameData gameData, Map<String, Resource> resources) {
        if(quernTechnology == null){
            quernTechnology = createTechnology(
                    "quern",
                    "Quern",
                    Set.of(
                            Set.of("tfc:quern")
                    ),
                    resources
            );
        }
        return quernTechnology;

    }

    private Technology getOrCreateBloomeryTechnology(GameData gameData, Map<String, Resource> resources) {
        if(bloomeryTechnology == null){
            bloomeryTechnology = createTechnology(
                    "bloomery",
                    "Bloomery",
                    Set.of(
                            Set.of("tfc:bloomery"),
                            TagUtils.getItemTagResourceIds("minecraft:stone_bricks", gameData)
                    ),
                    resources
            );
        }
        return bloomeryTechnology;

    }


    private Technology getOrCreateBowlTechnology(GameData gameData, Map<String, Resource> resources) {
        if(bowlTechnology == null){
            bowlTechnology = createTechnology(
                    "bowl",
                    "Bowl",
                    Set.of(
                            TagUtils.getItemTagResourceIds("c:bowls", gameData)
                    ),
                    resources
            );
        }
        return bowlTechnology;

    }

    private Technology getOrCreateChiselTechnology(GameData gameData, Map<String, Resource> resources) {
        if(chiselTechnology == null){
            chiselTechnology = createTechnology(
                    "chisel",
                    "Chisel",
                    Set.of(
                            TagUtils.getItemTagResourceIds("c:tools/chisel", gameData)
                    ),
                    resources
            );
        }
        return chiselTechnology;

    }

    private Technology getOrCreatePotTechnology(GameData gameData, Map<String, Resource> resources) {
        if(potTechnology == null){
            potTechnology = createTechnology(
                    "pot",
                    "Pot (on a firepit)",
                    Set.of(
                            Set.of("tfc:ceramic/pot"),
                            TagUtils.getItemTagResourceIds("minecraft:logs", gameData),
                            Set.of("minecraft:stick")
                    ),
                    resources
            );

        }
        return potTechnology;

    }


    private Technology createTechnology(String id, String name, Set<Set<String>> requirements, Map<String, Resource> resources){
        Technology technology = new Technology(id,name);

        int requirementGroupIndex = 0;
        for(Set<String> requirement : requirements){
            for (String resourceId : requirement) {
                Resource resource = resources.get(resourceId);
                if (resource == null) {
                    throw new IllegalArgumentException(
                            "Unknown technology requirement resource: "
                                    + resourceId
                    );
                }
                technology.addResourceRequirement(requirementGroupIndex, resource);
            }
            requirementGroupIndex++;
        }
        return technology;

    }


}
