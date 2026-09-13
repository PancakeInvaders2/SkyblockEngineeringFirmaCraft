package com.pancake.tfc.skyblock.design.reachability.tester.service;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.Resource;
import com.pancake.tfc.skyblock.design.reachability.tester.entities.Technology;

import java.util.Set;

public record ReachabilityResult(
        Set<Resource> reachableResources,
        Set<Technology> reachableTechnologies,
        Set<Resource> infinitelyReachableResources
) {}