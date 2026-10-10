package com.pancake.tfc.skyblock.design.persistence.repositories;

import com.pancake.tfc.skyblock.design.persistence.entities.Process;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository
        extends JpaRepository<Process, String> {
}