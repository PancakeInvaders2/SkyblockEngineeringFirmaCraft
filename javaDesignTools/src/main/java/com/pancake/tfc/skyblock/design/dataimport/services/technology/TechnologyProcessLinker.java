package com.pancake.tfc.skyblock.design.dataimport.services.technology;

import com.pancake.tfc.skyblock.design.persistence.entities.Process;
import com.pancake.tfc.skyblock.design.persistence.entities.Technology;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TechnologyProcessLinker {

    private static final Set<String> PROCESS_TYPES_WITHOUT_TECHNOLOGY =
            Set.of();


    // TODO
    // Process types:
    // [
    // tfc:casting,
    // tfc:anvil,
    // tfc:alloy,
    // tfc:pot_soup,
    // tfc:advanced_shapeless_crafting,
    // tfc:barrel_instant,
    // tfc:welding,
    // tfc:collapse,
    // minecraft:smelting,
    // tfc:barrel_sealed,
    // tfc:glassworking,
    // tfc:scraping,
    // minecraft:blasting,
    // tfc:blast_furnace,
    // tfc:barrel_instant_fluid,
    // tfc:knapping,
    // tfc:loom, tfc:quern,
    // tfc:bloomery,
    // minecraft:campfire_cooking,
    // tfc:heating,
    // custom:clicking_pot_with_bowl,
    // minecraft:crafting_shaped,
    // minecraft:crafting_shapeless,
    // tfc:chisel,
    // minecraft:smoking,
    // tfc:pot,
    // tfc:advanced_shaped_crafting
    // ]

    public List<Technology> linkProcessesToTechnology(Map<String, List<Process>> processesByType) {

        for(String processTypeThatDoesntRequireATechnology : PROCESS_TYPES_WITHOUT_TECHNOLOGY){
            for( Process process : processesByType.get(processTypeThatDoesntRequireATechnology) ) {
                process.setTechnologyRequired(false);
            }
        }

        // TODO Create one Technology for each process type that requires technology,
        //      and link all processes of that type to it.
        //
        // TODO Define the resource requirements of each Technology.
        //      ex: crafting 3x3 requires a crafting table.

        return List.of();
    }


}
