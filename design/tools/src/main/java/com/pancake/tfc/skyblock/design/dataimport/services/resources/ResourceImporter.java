package com.pancake.tfc.skyblock.design.dataimport.services.resources;

import com.pancake.tfc.skyblock.design.dataimport.services.process.ParsedProcess;
import com.pancake.tfc.skyblock.design.persistence.entities.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ResourceImporter {

    public Map<String, Resource> importResources(
            Collection<ParsedProcess> processes
    ) {
        Map<String, Resource> resources = new HashMap<>();

        for (ParsedProcess process : processes) {
            for (Set<String> inputGroup : process.inputGroups()) {
                for (String resourceId : inputGroup) {
                    addResource(resources, resourceId);
                }
            }

            for (String resourceId : process.outputs()) {
                addResource(resources, resourceId);
            }
        }

        return resources;
    }

    private void addResource(
            Map<String, Resource> resources,
            String resourceId
    ) {
        if (!resources.containsKey(resourceId)) {
            resources.put(resourceId, new Resource(resourceId));
        }
    }
}