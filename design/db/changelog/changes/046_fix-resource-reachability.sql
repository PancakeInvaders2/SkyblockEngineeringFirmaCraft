--liquibase formatted sql


--changeset tfc:046-fix-resource-reachability splitStatements:false

DROP FUNCTION yet_to_be_implemented();

CREATE FUNCTION yet_to_be_implemented()
RETURNS TABLE(type text, id text, issue text)
LANGUAGE sql
AS $function$

    SELECT
        'resource' AS type,
        r.id,
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

    SELECT
        'process' AS type,
        p.id,
        'no technology unlock' AS issue
    FROM process p
    WHERE p.technology_required
    AND NOT EXISTS (
        SELECT 1
        FROM technology_process_unlock tpu
        WHERE tpu.process_id = p.id
    )

    UNION ALL

    SELECT
        'process' AS type,
        p.id,
        'no inputs' AS issue
    FROM process p
    WHERE NOT EXISTS (
        SELECT 1
        FROM process_input pi
        WHERE pi.process_id = p.id
    )

    UNION ALL

    SELECT
        'process' AS type,
        p.id,
        'no outputs' AS issue
    FROM process p
    WHERE NOT EXISTS (
        SELECT 1
        FROM process_output po
        WHERE po.process_id = p.id
    )

    UNION ALL

    SELECT
        'technology' AS type,
        t.id,
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

    ORDER BY type, issue;

$function$;