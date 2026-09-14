package com.pancake.tfc.skyblock.design.repositories;

import com.pancake.tfc.skyblock.design.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository
        extends JpaRepository<Resource, String> {
}