package com.pancake.tfc.skyblock.design.reachability.tester.service;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.*;
import com.pancake.tfc.skyblock.design.reachability.tester.entities.Process;
import com.pancake.tfc.skyblock.design.reachability.tester.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final ScenarioResourceRepository scenarioResourceRepository;

    public ReachabilityService(
            ScenarioRepository scenarioRepository,
            ResourceRepository resourceRepository,
            ProcessRepository processRepository,
            TechnologyRepository technologyRepository,
            ScenarioResourceRepository scenarioResourceRepository) {

        this.scenarioRepository = scenarioRepository;
        this.resourceRepository = resourceRepository;
        this.processRepository = processRepository;
        this.technologyRepository = technologyRepository;
        this.scenarioResourceRepository = scenarioResourceRepository;
    }

    public ReachabilityResult infiniteReachableResources(String scenarioId) {

        Scenario scenario = loadScenario(scenarioId);

        List<Resource> allResources = resourceRepository.findAll();
        List<Process> allProcesses = processRepository.findAll();
        List<Technology> allTechnologies = technologyRepository.findAll();

        List<ModelIssue> issues =
                validateModel(allResources, allProcesses, allTechnologies);

        if (issues.isEmpty()) {
            LOG.error("Model validation OK");
        }
        else {
            LOG.error("Model validation failed with {} issue(s)", issues.size());

            issues.forEach(issue ->
                    LOG.error(
                            "{} {} ({}): {}",
                            issue.type(),
                            issue.id(),
                            issue.name(),
                            issue.issue()
                    )
            );

            throw new IllegalStateException(
                    "Model validation failed with " + issues.size() + " issue(s)"
            );
        }

        RegularReachabilityResult reachable =
                calculateReachability(scenario, allResources, allProcesses, allTechnologies);


        Set<Resource> initialResources = scenario.getResources().stream()
                .filter(ScenarioResource::isInfinite)
                .map(ScenarioResource::getResource)
                .collect(Collectors.toSet());

        LOG.info(
                "Testing infinite reachability for scenario: {}",
                scenarioId
        );

        Set<Resource> infinitelyReachableResources = calculateInfiniteReachability(
                initialResources,
                allResources,
                allProcesses,
                reachable.reachableTechnologies()
        );

        return new ReachabilityResult(
                reachable.reachableResources(),
                reachable.reachableTechnologies(),
                infinitelyReachableResources);
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

    private RegularReachabilityResult calculateReachability(
            Scenario scenario,
            List<Resource> allResources,
            List<Process> allProcesses,
            List<Technology> allTechnologies) {

        Set<Resource> initialResources = scenario.getResources().stream()
                .map(ScenarioResource::getResource)
                .collect(Collectors.toSet());

        LOG.info("Testing reachability for scenario: {}", scenario.getId());

        Set<Resource> reachable = new HashSet<>(initialResources);

        Set<Technology> reachableTechnologies = new HashSet<>();

        boolean changed = true;
        int iteration = 0;

        while (changed) {
            changed = false;
            iteration++;

            LOG.debug("Reachability iteration {}", iteration);

            for (Technology technology : allTechnologies) {
                if (reachableTechnologies.contains(technology)) {
                    continue;
                }

                if (reachable.containsAll(technology.getResourceRequirements())) {

                    reachableTechnologies.add(technology);

                    LOG.info(
                            "Reached technology {}",
                            technology.getId()
                    );

                    changed = true;
                }
            }

            for (Process process : allProcesses) {
                if (inputsReachable(process, reachable)
                        && technologyAvailable(process, reachableTechnologies)) {

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
        LOG.info(
                "Found {} reachable technologies",
                reachableTechnologies.size()
        );
        LOG.info("----------");


        return new RegularReachabilityResult(
                Set.copyOf(reachable),
                Set.copyOf(reachableTechnologies)
        );

    }

    private boolean inputsReachable(
            Process process,
            Set<Resource> reachable) {

        return reachable.containsAll(process.getInputs());
    }

    private boolean technologyAvailable(
            Process process,
            Set<Technology> availableTechnologies) {

        if (!process.isTechnologyRequired()) {
            return true;
        }

        return process.getUnlockedBy().stream()
                .anyMatch(availableTechnologies::contains);
    }

    private Set<Resource> calculateInfiniteReachability(
            Set<Resource> initialResources,
            List<Resource> allResources,
            List<Process> allProcesses,
            Set<Technology> availableTechnologies) {

        LOG.info(
                "Calculating infinitely reachable resources using {} available resources and {} available technologies",
                initialResources.size(),
                availableTechnologies.size()
        );

        Set<Resource> reachable = new HashSet<>(initialResources);

        boolean changed = true;
        int iteration = 0;

        while (changed) {
            changed = false;
            iteration++;

            LOG.debug(
                    "Infinite reachability iteration {}",
                    iteration
            );

            for (Process process : allProcesses) {
                if (inputsReachable(process, reachable)
                        && technologyAvailable(
                        process,
                        availableTechnologies)) {

                    for (Resource output : process.getOutputs()) {
                        if (reachable.add(output)) {
                            LOG.info(
                                    "Reached infinite {} through process {}",
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
                "Infinite reachability converged after {} iterations",
                iteration
        );

        Set<Resource> unreachable = allResources.stream()
                .filter(resource -> !reachable.contains(resource))
                .collect(Collectors.toSet());

        LOG.info(
                "Infinite reachability reached {} / {} resources",
                reachable.size(),
                allResources.size()
        );

        LOG.info("----------");
        LOG.info(
                "Not infinitely reachable: {}",
                unreachable.stream()
                        .map(Resource::getId)
                        .sorted()
                        .toList()
        );
        LOG.info("----------");

        return Set.copyOf(reachable);
    }

    private List<ModelIssue> validateModel(
            List<Resource> resources,
            List<Process> processes,
            List<Technology> technologies) {

        List<ModelIssue> issues = new ArrayList<>();

        Scenario vanillaScenario = scenarioRepository
                .findById(ScenarioEnum.VANILLA_TFC.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Vanilla TFC scenario is missing"
                        ));

        Set<String> scenarioResourceIds = vanillaScenario.getResources().stream()
                .map(ScenarioResource::getResource)
                .map(Resource::getId)
                .collect(Collectors.toSet());

        Set<String> processOutputResourceIds = processes.stream()
                .flatMap(process -> process.getOutputs().stream())
                .map(Resource::getId)
                .collect(Collectors.toSet());

        Set<String> technologyIds = technologies.stream()
                .map(Technology::getId)
                .collect(Collectors.toSet());

        for (Resource resource : resources) {
            if (!scenarioResourceIds.contains(resource.getId())
                    && !processOutputResourceIds.contains(resource.getId())
                    && !technologyIds.contains(resource.getId())) {

                issues.add(new ModelIssue(
                        "resource",
                        resource.getId(),
                        resource.getName(),
                        "no scenario or process output"
                ));
            }
        }

        for (Process process : processes) {

            if (process.isTechnologyRequired()
                    && process.getUnlockedBy().isEmpty()) {

                issues.add(new ModelIssue(
                        "process",
                        process.getId(),
                        process.getName(),
                        "no technology unlock"
                ));
            }

            if (process.getInputs().isEmpty()) {
                issues.add(new ModelIssue(
                        "process",
                        process.getId(),
                        process.getName(),
                        "no inputs"
                ));
            }

            if (process.getOutputs().isEmpty()) {
                issues.add(new ModelIssue(
                        "process",
                        process.getId(),
                        process.getName(),
                        "no outputs"
                ));
            }
        }

        for (Technology technology : technologies) {

            if (technology.getResourceRequirements().isEmpty()
                    && technology.getUnlockedProcesses().isEmpty()) {

                issues.add(new ModelIssue(
                        "technology",
                        technology.getId(),
                        technology.getName(),
                        "no prerequisites or process unlocks"
                ));
            }
        }

        return issues;
    }

    private record RegularReachabilityResult(
            Set<Resource> reachableResources,
            Set<Technology> reachableTechnologies
    ) {}
}

