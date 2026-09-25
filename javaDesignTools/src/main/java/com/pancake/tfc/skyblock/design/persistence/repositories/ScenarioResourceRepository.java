package com.pancake.tfc.skyblock.design.persistence.repositories;

import com.pancake.tfc.skyblock.design.persistence.entities.ScenarioResource;
import com.pancake.tfc.skyblock.design.persistence.entities.ScenarioResourceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScenarioResourceRepository
        extends JpaRepository<ScenarioResource, ScenarioResourceId> {

    @Modifying
    @Query(value =
        """
        insert into scenario_resource (scenario_id, resource_id, is_infinite )
        select
        :scenarioId,
        id as resource_id,
        :isInfinite
        from resource
        where id ilike :pattern;
        """, nativeQuery = true)
    void insertFromPattern(String scenarioId, String pattern, boolean isInfinite);

    @Modifying
    @Query(value =
            """
            insert into scenario_resource (scenario_id, resource_id, is_infinite )
            select
            :scenarioId,
            id as resource_id,
            :isInfinite
            from resource
            where id in (:ids);
            """, nativeQuery = true)
    void insertFromList(String scenarioId, List<String> ids, boolean isInfinite);
}