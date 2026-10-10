package com.pancake.tfc.skyblock.design.reachability.services;

import com.pancake.tfc.skyblock.design.persistence.entities.Resource;
import com.pancake.tfc.skyblock.design.persistence.entities.Technology;

import java.util.Set;

public record ReachabilityResult(
        Set<Resource> reachableResources,
        Set<Technology> reachableTechnologies,
        Set<Resource> infinitelyReachableResources
) {}