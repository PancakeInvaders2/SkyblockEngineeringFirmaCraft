package com.pancake.tfc.skyblock.design.repositories;

import com.pancake.tfc.skyblock.design.entities.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository
        extends JpaRepository<Scenario, String> {
}