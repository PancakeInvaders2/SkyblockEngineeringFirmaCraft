package com.pancake.tfc.skyblock.design.reachability.tester.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record ScenarioResourceId(
        String scenarioId,
        String resourceId
) {}