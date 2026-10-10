package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record TechnologyResourceRequirementId(
        String technologyId,
        int requirementGroup,
        String resourceId
) {
}