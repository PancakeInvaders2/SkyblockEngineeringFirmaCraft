package com.pancake.tfc.skyblock.design.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record ScenarioResourceId(
        String scenarioId,
        String resourceId
) {}