--liquibase formatted sql

--changeset tfc:010-remove-source-and-process-metadata splitStatements:false

-- Recreate the validation function without the obsolete source/resource_source
-- and technology requirement relationships.
DROP FUNCTION yet_to_be_implemented();

-- A scenario now defines which resources exist in the world.
-- There is therefore no need for a separate source abstraction.
DROP TABLE resource_source;

DROP TABLE source;

-- The progression graph models whether a resource participates in a process,
-- not recipe quantities or execution semantics.
ALTER TABLE process_input
    DROP COLUMN quantity,
    DROP COLUMN consumed,
    DROP COLUMN notes;

ALTER TABLE process_output
    DROP COLUMN quantity,
    DROP COLUMN chance,
    DROP COLUMN notes;

CREATE FUNCTION yet_to_be_implemented()
RETURNS TABLE (
    type TEXT,
    id TEXT,
    name TEXT,
    issue TEXT
)
LANGUAGE SQL
AS $$
    -- Resources with no way of existing in the graph.
    --
    -- A resource is considered implemented if it either:
    --   - exists in at least one scenario, or
    --   - is produced by a process.
    SELECT
        'resource' AS type,
        r.id,
        r.name,
        'no scenario or process output' AS issue
    FROM resource r
    WHERE NOT EXISTS (
        SELECT 1
        FROM scenario_resource sr
        WHERE sr.resource_id = r.id
    )
    AND NOT EXISTS (
        SELECT 1
        FROM process_output po
        WHERE po.resource_id = r.id
    )

    UNION ALL

    -- Processes with no technology unlocking them.
    SELECT
        'process' AS type,
        p.id,
        p.name,
        'no technology unlock' AS issue
    FROM process p
    WHERE NOT EXISTS (
        SELECT 1
        FROM technology_process_unlock tpu
        WHERE tpu.process_id = p.id
    )

    UNION ALL

    -- Processes with no inputs.
    SELECT
        'process' AS type,
        p.id,
        p.name,
        'no inputs' AS issue
    FROM process p
    WHERE NOT EXISTS (
        SELECT 1
        FROM process_input pi
        WHERE pi.process_id = p.id
    )

    UNION ALL

    -- Processes with no outputs.
    SELECT
        'process' AS type,
        p.id,
        p.name,
        'no outputs' AS issue
    FROM process p
    WHERE NOT EXISTS (
        SELECT 1
        FROM process_output po
        WHERE po.process_id = p.id
    )

    UNION ALL

    -- Technologies with no resource prerequisites.
    SELECT
        'technology' AS type,
        t.id,
        t.name,
        'no prerequisite' AS issue
    FROM technology t
    WHERE NOT EXISTS (
        SELECT 1
        FROM technology_resource_requirement trr
        WHERE trr.technology_id = t.id
    )

    ORDER BY type, name, issue;
$$;

--rollback DROP FUNCTION yet_to_be_implemented();