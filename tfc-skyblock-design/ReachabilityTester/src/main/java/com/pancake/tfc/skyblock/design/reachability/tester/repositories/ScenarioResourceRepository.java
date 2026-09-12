package com.pancake.tfc.skyblock.design.reachability.tester.repositories;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.ScenarioResource;
import com.pancake.tfc.skyblock.design.reachability.tester.entities.ScenarioResourceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioResourceRepository
        extends JpaRepository<ScenarioResource, ScenarioResourceId> {
}