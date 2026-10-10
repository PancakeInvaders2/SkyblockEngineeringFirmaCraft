package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record ScenarioResourceId(
        String scenarioId,
        String resourceId
) {}