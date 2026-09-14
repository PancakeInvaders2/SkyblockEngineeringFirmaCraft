package com.pancake.tfc.skyblock.design.services.reachability;

import com.pancake.tfc.skyblock.design.entities.Resource;
import com.pancake.tfc.skyblock.design.entities.Technology;

import java.util.Set;

public record ReachabilityResult(
        Set<Resource> reachableResources,
        Set<Technology> reachableTechnologies,
        Set<Resource> infinitelyReachableResources
) {}