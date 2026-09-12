package com.pancake.tfc.skyblock.design.reachability.tester.service;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.*;
import com.pancake.tfc.skyblock.design.reachability.tester.entities.Process;
import com.pancake.tfc.skyblock.design.reachability.tester.repositories.ProcessRepository;
import com.pancake.tfc.skyblock.design.reachability.tester.repositories.ResourceRepository;
import com.pancake.tfc.skyblock.design.reachability.tester.repositories.ScenarioRepository;
import com.pancake.tfc.skyblock.design.reachability.tester.repositories.TechnologyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReachabilityService {

    private static final Logger LOG =
            LogManager.getLogger(ReachabilityService.class);

    private final ScenarioRepository scenarioRepository;
    private final ResourceRepository resourceRepository;
    private final ProcessRepository processRepository;
    private final TechnologyRepository technologyRepository;

    public ReachabilityService(
            ScenarioRepository scenarioRepository,
            ResourceRepository resourceRepository,
            ProcessRepository processRepository,
            TechnologyRepository technologyRepository) {

        this.scenarioRepository = scenarioRepository;
        this.resourceRepository = resourceRepository;
        this.processRepository = processRepository;
        this.technologyRepository = technologyRepository;
    }

    public ReachabilityResult reachableResources(String scenarioId) {

        Scenario scenario = loadScenario(scenarioId);

        Set<Resource> initialResources = scenario.getResources().stream()
                .map(ScenarioResource::getResource)
                .collect(Collectors.toSet());

        LOG.info("Testing reachability for scenario: {}", scenarioId);

        return calculateReachability(initialResources);
    }

    public ReachabilityResult infiniteReachableResources(String scenarioId) {

        Scenario scenario = loadScenario(scenarioId);

        Set<Resource> initialResources = scenario.getResources().stream()
                .filter(ScenarioResource::isInfinite)
                .map(ScenarioResource::getResource)
                .collect(Collectors.toSet());

        LOG.info(
                "Testing infinite reachability for scenario: {}",
                scenarioId
        );

        return calculateReachability(initialResources);
    }

    public ReachabilityResult reachableResourcesFrom(
            Set<Resource> initialResources) {

        LOG.info(
                "Testing reachability from {} initial resources",
                initialResources.size()
        );

        return calculateReachability(initialResources);
    }

    private Scenario loadScenario(String scenarioId) {

        return scenarioRepository
                .findById(scenarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unknown scenario: " + scenarioId
                        )
                );
    }

    private ReachabilityResult calculateReachability(
            Set<Resource> initialResources) {

        List<Resource> allResources = resourceRepository.findAll();
        List<Process> allProcesses = processRepository.findAll();
        List<Technology> allTechnologies = technologyRepository.findAll();

        LOG.info("Loaded {} processes", allProcesses.size());
        LOG.info("Loaded {} technologies", allTechnologies.size());
        LOG.info("Loaded {} resources", allResources.size());

        Set<Resource> reachable = new HashSet<>(initialResources);

        LOG.info(
                "Starting with {} reachable resources",
                reachable.size()
        );

        boolean changed = true;
        int iteration = 0;

        while (changed) {
            changed = false;
            iteration++;

            LOG.debug("Reachability iteration {}", iteration);

            for (Process process : allProcesses) {
                if (inputsReachable(process, reachable)
                        && technologyAvailable(process, reachable)){

                    for (Resource output : process.getOutputs()) {
                        if (reachable.add(output)) {
                            LOG.info(
                                    "Reached {} through process {}",
                                    output.getId(),
                                    process.getId()
                            );

                            changed = true;
                        }
                    }
                }
            }

            LOG.info("----");

        }

        LOG.info(
                "Reachability converged after {} iterations",
                iteration
        );

        Set<Resource> unreachable = allResources.stream()
                .filter(resource -> !reachable.contains(resource))
                .collect(Collectors.toSet());

        LOG.info(
                "Reachability reached {} / {} resources",
                reachable.size(),
                allResources.size()
        );

        LOG.info("----------");
        LOG.info(
                "Not reached: {}",
                unreachable.stream()
                        .map(Resource::getId)
                        .sorted()
                        .toList()
        );
        LOG.info("----------");

        diagnoseUnreachable(unreachable, allProcesses);

        return new ReachabilityResult(Set.copyOf(reachable));
    }

    private boolean inputsReachable(
            Process process,
            Set<Resource> reachable) {

        return reachable.containsAll(process.getInputs());
    }

    private boolean technologyAvailable(
            Process process,
            Set<Resource> reachable) {

        if (!process.isTechnologyRequired()) {
            return true;
        }

        return process.getUnlockedBy().stream()
                .anyMatch(technology ->
                        technology.getResourceRequirements().stream()
                                .allMatch(reachable::contains)
                );
    }

    private void diagnoseUnreachable(
            Set<Resource> unreachable,
            List<Process> allProcesses
    ) {
        if(!unreachable.isEmpty()) {
            LOG.info("Diagnosing unreachable resources");
            LOG.info("----");

            for (Resource resource : unreachable) {
                List<Process> producingProcesses = allProcesses.stream()
                        .filter(process -> process.getOutputs().contains(resource))
                        .toList();

                if (producingProcesses.isEmpty()) {
                    LOG.info(
                            "{}: no scenario source and no process produces it",
                            resource.getId()
                    );
                }
                else {

                    for (Process process : producingProcesses) {
                        LOG.info(
                                "{}: produced by process {}",
                                resource.getId(),
                                process.getId()
                        );

                        if (!process.getInputs().isEmpty()) {
                            LOG.info(
                                    "  inputs: {}",
                                    process.getInputs().stream()
                                            .map(Resource::getId)
                                            .sorted()
                                            .toList()
                            );
                        }

                        if (process.isTechnologyRequired()) {
                            process.getUnlockedBy().forEach(technology -> {
                                LOG.info(
                                        "  technology {} requires: {}",
                                        technology.getId(),
                                        technology.getResourceRequirements().stream()
                                                .map(Resource::getId)
                                                .sorted()
                                                .toList()
                                );
                            });
                        }
                    }
                }

                LOG.info("----");

            }
        }
    }
}