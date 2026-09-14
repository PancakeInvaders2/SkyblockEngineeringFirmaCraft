package com.pancake.tfc.skyblock.design.repositories;

import com.pancake.tfc.skyblock.design.entities.ScenarioResource;
import com.pancake.tfc.skyblock.design.entities.ScenarioResourceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioResourceRepository
        extends JpaRepository<ScenarioResource, ScenarioResourceId> {
}