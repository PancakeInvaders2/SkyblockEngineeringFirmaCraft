package com.pancake.tfc.skyblock.design.persistence.repositories;

import com.pancake.tfc.skyblock.design.persistence.entities.Technology;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnologyRepository
        extends JpaRepository<Technology, String> {
}