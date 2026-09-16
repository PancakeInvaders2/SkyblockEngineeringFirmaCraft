package com.pancake.tfc.skyblock.design.dataimport.services.process;

import com.pancake.tfc.skyblock.design.dataimport.services.recipe.ParsedProcess;
import com.pancake.tfc.skyblock.design.persistence.entities.Resource;
import com.pancake.tfc.skyblock.design.persistence.entities.Process;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProcessImporter {

    public List<Process> importProcesses(
            Collection<ParsedProcess> parsedProcesses,
            Map<String, Resource> resources
    ) {
        List<Process> processes = new ArrayList<>();

        for (ParsedProcess parsedProcess : parsedProcesses) {
            Process process = new Process(
                    parsedProcess.id(),
                    parsedProcess.name(),
                    parsedProcess.type(),
                    true
            );

            for (int inputGroup = 0;
                 inputGroup < parsedProcess.inputGroups().size();
                 inputGroup++) {

                Set<String> resourceIds =
                        parsedProcess.inputGroups().get(inputGroup);

                for (String resourceId : resourceIds) {
                    Resource resource = resources.get(resourceId);

                    if (resource == null) {
                        throw new IllegalArgumentException(
                                "Unknown input resource: " + resourceId
                                        + " for process: " + parsedProcess.id()
                        );
                    }

                    process.addInput(inputGroup, resource);
                }
            }

            for (String resourceId : parsedProcess.outputs()) {
                Resource resource = resources.get(resourceId);

                if (resource == null) {
                    throw new IllegalArgumentException(
                            "Unknown output resource: " + resourceId
                                    + " for process: " + parsedProcess.id()
                    );
                }

                process.addOutput(resource);
            }

            processes.add(process);
        }

        return processes;
    }
}