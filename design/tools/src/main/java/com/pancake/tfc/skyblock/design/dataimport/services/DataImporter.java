package com.pancake.tfc.skyblock.design.dataimport.services;

import com.pancake.tfc.skyblock.design.dataimport.DataImportApplication;
import com.pancake.tfc.skyblock.design.dataimport.services.scenario.ScenarioResourceAssigner;
import com.pancake.tfc.skyblock.design.persistence.entities.Process;
import com.pancake.tfc.skyblock.design.persistence.entities.Resource;
import com.pancake.tfc.skyblock.design.persistence.entities.Technology;
import com.pancake.tfc.skyblock.design.persistence.repositories.ProcessRepository;
import com.pancake.tfc.skyblock.design.persistence.repositories.ResourceRepository;
import com.pancake.tfc.skyblock.design.persistence.repositories.ScenarioResourceRepository;
import com.pancake.tfc.skyblock.design.persistence.repositories.TechnologyRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataImporter {


    private static final Logger LOG =
            LogManager.getLogger(DataImporter.class);

    private final ProcessRepository processRepo;
    private final ResourceRepository resourceRepo;
    private final ScenarioResourceRepository scenarioResourceRepo;
    private final TechnologyRepository technologyRepo;
    private final ScenarioResourceAssigner scenarioResourceAssigner;

    public DataImporter(
            ProcessRepository processRepo,
            ResourceRepository resourceRepo,
            ScenarioResourceRepository scenarioResourceRepo,
            TechnologyRepository technologyRepo,
            ScenarioResourceAssigner scenarioResourceAssigner){
        this.processRepo = processRepo;
        this.resourceRepo = resourceRepo;
        this.scenarioResourceRepo = scenarioResourceRepo;
        this.technologyRepo = technologyRepo;
        this.scenarioResourceAssigner = scenarioResourceAssigner;

    }

    @Transactional
    public void importData(Map<String, Resource> resources, List<Process> processes, Map<String, Technology> technologies) {
        LOG.info("#################################");

        LOG.info("Deleting the existing data");
        scenarioResourceRepo.deleteAll();
        technologyRepo.deleteAll();
        processRepo.deleteAll();
        resourceRepo.deleteAll();

        LOG.info("Populating with the parsed data");
        resourceRepo.saveAll(resources.values());
        processRepo.saveAll(processes);
        technologyRepo.saveAll(technologies.values());

        LOG.info("Linking scenarios with the resources they contain");
        scenarioResourceAssigner.assignVanillaTfcResources();

        LOG.info("DONE");
    }
}
