package com.pancake.tfc.skyblock.design.persistence.repositories;

import com.pancake.tfc.skyblock.design.persistence.entities.ScenarioResource;
import com.pancake.tfc.skyblock.design.persistence.entities.ScenarioResourceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioResourceRepository
        extends JpaRepository<ScenarioResource, ScenarioResourceId> {
}