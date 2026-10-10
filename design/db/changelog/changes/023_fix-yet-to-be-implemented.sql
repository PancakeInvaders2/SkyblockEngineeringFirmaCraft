--liquibase formatted sql

--changeset tfc:023-fix-yet-to-be-implemented splitStatements:false

DROP FUNCTION yet_to_be_implemented();

CREATE FUNCTION yet_to_be_implemented()
RETURNS TABLE (
    type TEXT,
    id TEXT,
    name TEXT,
    issue TEXT
)
LANGUAGE SQL
AS $$
    -- Resources that have no known way of existing in a scenario
    -- and no process that produces them.
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
    AND NOT EXISTS (SELECT 1 FROM technology t WHERE t.id = r.id)

    UNION ALL

    -- Processes that are not made available by any technology.
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

    -- Processes without any inputs.
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

    -- Processes without any outputs.
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

    -- Technologies that neither require anything nor unlock anything.
    SELECT
        'technology' AS type,
        t.id,
        t.name,
        'no prerequisites or process unlocks' AS issue
    FROM technology t
    WHERE NOT EXISTS (
        SELECT 1
        FROM technology_resource_requirement trr
        WHERE trr.technology_id = t.id
    )
    AND NOT EXISTS (
        SELECT 1
        FROM technology_process_unlock tpu
        WHERE tpu.technology_id = t.id
    )

    ORDER BY type, name, issue;
$$;

--rollback DROP FUNCTION yet_to_be_implemented();