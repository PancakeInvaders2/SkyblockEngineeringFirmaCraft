package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Embeddable
public record ProcessInputId(
        String processId,
        int inputGroup,
        String resourceId
) {
}