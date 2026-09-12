package com.pancake.tfc.skyblock.design.reachability.tester.service;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.Resource;

import java.util.Set;

public record ReachabilityResult(
        Set<Resource> reachableResources
) {
}