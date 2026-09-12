package com.pancake.tfc.skyblock.design.reachability.tester.repositories;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository
        extends JpaRepository<Resource, String> {
}