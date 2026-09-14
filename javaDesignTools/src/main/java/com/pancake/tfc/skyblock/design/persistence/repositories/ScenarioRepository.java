package com.pancake.tfc.skyblock.design.persistence.repositories;

import com.pancake.tfc.skyblock.design.persistence.entities.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository
        extends JpaRepository<Scenario, String> {
}