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

    // TODO handle tags of required resources instead of a single resource id
    private Technology craftingTableTechnology = null;
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

    public Map<String, Technology> linkProcessesToTechnology(
            Map<String, List<Process>> processesByType,
            GameData gameData,
            Map<String, Resource> resources) {

        Map<String, Technology> technologiesById = new HashMap<>();
        for(Map.Entry<String, List<Process>> processesOfType : processesByType.entrySet()  ){

            ProcessType processType = ProcessType.fromRecipeType(processesOfType.getKey());

            for( Process process : processesOfType.getValue()){

                Technology technology = getOrCreateTechnology(processType, process.getSubType(), gameData, resources);
                if(technology == null){
                    process.setTechnologyRequired(false);
                }
                else {
                    technology.getUnlockedProcesses().add(process);
                    technologiesById.put(technology.getId(), technology);
                }
            }
        }

        // TODO Create one Technology for each process type that requires technology,
        //      and link all processes of that type to it.
        //
        // TODO Define the resource requirements of each Technology.
        //      ex: crafting 3x3 requires a crafting table (called workbench in tfc).

        return technologiesById;
    }

    private Technology getOrCreateTechnology(ProcessType processType,
                                             Optional<ProcessSubtype> subType,
                                             GameData gameData,
                                             Map<String, Resource> resources) {
        
        return switch (processType){
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS, ADVANCED_SHAPED_CRAFTING, ADVANCED_SHAPELESS_CRAFTING -> {
                if( ProcessSubtype.CRAFTING_3x3.equals(subType.get())){
                    yield getOrCreateCraftingTableTechnology(gameData, resources);
                }
                yield null;
            }
            // TODO do the rest of the process types
            case CASTING -> null;
            case ANVIL -> null;
            case ALLOY -> null;
            case POT_SOUP -> null;
            case BARREL_INSTANT -> null;
            case WELDING -> null;
            case COLLAPSE -> null;
            case SMELTING -> null;
            case BARREL_SEALED -> null;
            case GLASSWORKING -> null;
            case SCRAPING -> null;
            case BLASTING -> null;
            case BLAST_FURNACE -> null;
            case BARREL_INSANT_FLUID -> null;
            case KNAPPING -> null;
            case LOOM -> null;
            case QUERN -> null;
            case BLOOMERY -> null;
            case CAMPFIRE_COOKING -> null;
            case HEATING -> null;
            case CLICKING_POT_WITH_BOWL -> null;
            case CHISEL -> null;
            case SMOKING -> null;
            case POT -> null;
        };
        
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
