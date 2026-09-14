package com.pancake.tfc.skyblock.design.repositories;

import com.pancake.tfc.skyblock.design.entities.Process;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository
        extends JpaRepository<Process, String> {
}