package com.pancake.tfc.skyblock.design.persistence.repositories;

import com.pancake.tfc.skyblock.design.persistence.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository
        extends JpaRepository<Resource, String> {
}