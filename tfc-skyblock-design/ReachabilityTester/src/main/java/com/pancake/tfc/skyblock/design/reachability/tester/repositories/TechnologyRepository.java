package com.pancake.tfc.skyblock.design.reachability.tester.repositories;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.Technology;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnologyRepository
        extends JpaRepository<Technology, String> {
}