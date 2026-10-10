--liquibase formatted sql

--changeset tfc:042-fix-resource-reachability splitStatements:false

DROP FUNCTION reachable_resources_from(TEXT[]);

CREATE FUNCTION reachable_resources_from(
    p_initial_resources TEXT[]
)
RETURNS TABLE (
    resource_id TEXT,
    resource_name TEXT,
    reached_by_process_id TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    changed BOOLEAN := TRUE;
    current_process RECORD;
    output_resource RECORD;
BEGIN
    CREATE TEMP TABLE reachable_resource (
        resource_id TEXT PRIMARY KEY,
        reached_by_process_id TEXT
    ) ON COMMIT DROP;

    -- Seed the working set.
    INSERT INTO reachable_resource (
        resource_id,
        reached_by_process_id
    )
    SELECT
        initial.resource_id,
        NULL
    FROM unnest(p_initial_resources) AS initial(resource_id);

    -- Fixed-point calculation.
    WHILE changed LOOP
        changed := FALSE;

        FOR current_process IN
            SELECT p.id
            FROM process p
            WHERE
                -- Every process input must be reachable.
                NOT EXISTS (
                    SELECT 1
                    FROM process_input pi
                    WHERE pi.process_id = p.id
                      AND NOT EXISTS (
                          SELECT 1
                          FROM reachable_resource rr
                          WHERE rr.resource_id = pi.resource_id
                      )
                )

                AND

                (
                    -- Intrinsic process.
                    NOT p.technology_required

                    OR

                    -- At least one technology unlocking this process
                    -- is available.
                    EXISTS (
                        SELECT 1
                        FROM technology_process_unlock tpu
                        WHERE tpu.process_id = p.id
                          AND NOT EXISTS (
                              SELECT 1
                              FROM technology_resource_requirement trr
                              WHERE trr.technology_id = tpu.technology_id
                                AND NOT EXISTS (
                                    SELECT 1
                                    FROM reachable_resource rr
                                    WHERE rr.resource_id = trr.resource_id
                                )
                          )
                    )
                )
        LOOP
            FOR output_resource IN
                SELECT po.resource_id
                FROM process_output po
                WHERE po.process_id = current_process.id
            LOOP
                INSERT INTO reachable_resource (
                    resource_id,
                    reached_by_process_id
                )
                VALUES (
                    output_resource.resource_id,
                    current_process.id
                )
                ON CONFLICT (resource_id) DO NOTHING;

                IF FOUND THEN
                    changed := TRUE;
                END IF;
            END LOOP;
        END LOOP;
    END LOOP;

    RETURN QUERY
    SELECT
        rr.resource_id,
        r.name,
        rr.reached_by_process_id
    FROM reachable_resource rr
    JOIN resource r
        ON r.id = rr.resource_id
    ORDER BY r.name;
END;
$$;