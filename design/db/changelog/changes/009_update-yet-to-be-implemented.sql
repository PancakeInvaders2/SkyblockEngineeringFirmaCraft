--liquibase formatted sql

--changeset tfc:009-update-yet-to-be-implemented splitStatements:false

CREATE OR REPLACE FUNCTION yet_to_be_implemented()
RETURNS TABLE (
    type TEXT,
    id TEXT,
    name TEXT,
    issue TEXT
)
LANGUAGE SQL
AS $$
    -- Resources with no acquisition path
    SELECT
        'resource' AS type,
        r.id,
        r.name,
        'no acquisition path' AS issue
    FROM resource r
    WHERE NOT EXISTS (
        SELECT 1
        FROM resource_source rs
        WHERE rs.resource_id = r.id
    )
    AND NOT EXISTS (
        SELECT 1
        FROM process_output po
        WHERE po.resource_id = r.id
    )

    UNION ALL

    -- Processes with no technology unlocking them
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

    -- Processes with no inputs
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

    -- Processes with no outputs
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

    -- Technologies with no resource prerequisites
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