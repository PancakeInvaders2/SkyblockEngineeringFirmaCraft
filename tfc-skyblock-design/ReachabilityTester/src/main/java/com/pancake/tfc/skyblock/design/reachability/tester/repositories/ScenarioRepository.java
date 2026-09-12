package com.pancake.tfc.skyblock.design.reachability.tester.repositories;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository
        extends JpaRepository<Scenario, String> {
}