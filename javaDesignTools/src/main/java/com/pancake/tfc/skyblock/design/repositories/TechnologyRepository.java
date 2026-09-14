package com.pancake.tfc.skyblock.design.repositories;

import com.pancake.tfc.skyblock.design.entities.Technology;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnologyRepository
        extends JpaRepository<Technology, String> {
}