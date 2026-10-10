package com.pancake.tfc.skyblock.design.dataimport.services.scenario;

import com.pancake.tfc.skyblock.design.persistence.repositories.ResourceRepository;
import com.pancake.tfc.skyblock.design.persistence.repositories.ScenarioResourceRepository;
import com.pancake.tfc.skyblock.design.reachability.services.ScenarioEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScenarioResourceAssigner {
    private final ScenarioResourceRepository scenarioResourceRepo;
    private final ResourceRepository resourceRepo;


    public ScenarioResourceAssigner (ScenarioResourceRepository scenarioResourceRepo,
                                     ResourceRepository resourceRepo){
        this.scenarioResourceRepo = scenarioResourceRepo;
        this.resourceRepo = resourceRepo;
    }

    public void assignVanillaTfcResources(){

        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%ore%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:sand%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:plant%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:dirt%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:rooted_dirt%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:duff%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:rock%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:grass%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:raw_sandstone%", true);
        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:deposit%", true);

        scenarioResourceRepo.insertFromPattern(ScenarioEnum.VANILLA_TFC.getId(), "%tfc:entities%", true);
        scenarioResourceRepo.insertFromList(ScenarioEnum.VANILLA_TFC.getId(), List.of(
                "minecraft:entities/creeper",
                "minecraft:entities/zombie",
                "minecraft:entities/spider",
                "minecraft:entities/skeleton"), true);

    }

}
