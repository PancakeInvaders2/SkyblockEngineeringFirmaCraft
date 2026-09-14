package com.pancake.tfc.skyblock.design.reachability.tester.repositories;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.Process;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository
        extends JpaRepository<Process, String> {
}