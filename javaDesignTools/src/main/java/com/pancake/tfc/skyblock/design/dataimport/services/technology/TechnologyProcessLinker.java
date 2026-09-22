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

@Component
public class TechnologyProcessLinker {

    private Technology craftingTableTechnology = null;
    private Technology castingTechnology = null;
    private Technology alloyTechnology = null;
    private Technology anvilTechnology = null;
    private Technology smeltingTechnology = null;
    private Technology glassworkingTechnology = null;
    private Technology scrapingTechnology = null;
    private Technology blastingTechnology = null;
    private Technology blastFurnaceTechnology = null;
    private Technology barrelTechnology = null;
    private Technology loomTechnology = null;
    private Technology quernTechnology = null;
    private Technology bloomeryTechnology = null;
    private Technology heatingTechnology = null;
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
            case CASTING -> getOrCreateCastingTechnology(gameData, resources);
            case ANVIL, WELDING -> getOrCreateAnvilTechnology(gameData, resources);
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
            case HEATING -> getOrCreateHeatingTechnology(gameData, resources);
            case CLICKING_POT_WITH_BOWL -> getOrCreateBowlTechnology(gameData, resources);
            case CLICKING_RAW_ROCK_WITH_HAMMER -> null;
            case CHISEL -> getOrCreateChiselTechnology(gameData, resources);
        };
        
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
            ); // TODO make sure the alloy recipes have the wood and straw for the firepit as ingredient
        }
        return alloyTechnology;

    }

    private Technology getOrCreateAnvilTechnology(GameData gameData, Map<String, Resource> resources) {
        if(anvilTechnology == null){
            anvilTechnology = createTechnology(
                    "anvil",
                    "Anvil",
                    Set.of(
                            TagUtils.getItemTagResourceIds("tfc:anvils", gameData)
                    ),
                    resources
            ); // TODO extract the needed anvil tier from the recipes, make an anvil technology for each tier
        }
        return anvilTechnology;

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
            ); // TODO not quite sure which tfc aparatus can do minecraft:smelting recipe, if any
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
            ); // TODO see if any tfc aparatus can do minecraft blasting
        }
        return blastingTechnology;

    }

    private Technology getOrCreateBlastFurnaceTechnology(GameData gameData, Map<String, Resource> resources) {
        // TODO make sure the blast furnace processes have a tuyere as
        //   an ingredient since it breaks over time when using the blast furnace
        //   and the charcoal/fuel and the flux should also be ingredients
        if(blastFurnaceTechnology == null){
            blastFurnaceTechnology = createTechnology(
                    "blast_furnace",
                    "Blast furnace",
                    Set.of(
                            Set.of("tfc:blast_furnace"),
                            Set.of("tfc:reinforced_fire_bricks")
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
        } // TODO make sure the handstone is part of the process ingredients, since it breaks over time
        return quernTechnology;

    }

    private Technology getOrCreateBloomeryTechnology(GameData gameData, Map<String, Resource> resources) {
        // TODO make sure the bloomery processes have the charcoal as an ingredient
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

    private Technology getOrCreateHeatingTechnology(GameData gameData, Map<String, Resource> resources) {
        if(heatingTechnology == null){
            heatingTechnology = null; // TODO 4 technologies depending on the required temperature: firepit, pit_kiln, charcoal_forge, bellowed_forge
        } // TODO parse the required temperature, and add the fuels as ingredients
        return heatingTechnology;

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
            ); // TODO make sure pot recipes have the log fuel as an ingredient

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
