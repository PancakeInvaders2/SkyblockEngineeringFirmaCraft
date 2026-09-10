--liquibase formatted sql

--changeset tfc:008-cleanup-technology-relationships

-- A resource is acquired either from a source or as the output of a process.
-- A separate technology -> resource unlock relationship is therefore redundant.
DROP TABLE technology_resource_unlock;

-- Technologies do not require other technologies directly.
-- If a resource normally requires a particular technology to obtain,
-- that dependency belongs to the resource's acquisition path instead.
DROP TABLE technology_technology_requirement;

-- There is no distinct TFC mechanic where completing a process is itself
-- a prerequisite for obtaining a technology.
DROP TABLE technology_process_requirement;

-- The graph is the source of truth; process notes should not duplicate it.
ALTER TABLE process
    DROP COLUMN notes;

-- The graph is the source of truth; process descriptions should not
-- duplicate information represented by the process type and relationships.
ALTER TABLE process
    DROP COLUMN description;

-- Bellows are obtained through a crafting process unlocked by the
-- Bellows technology.
INSERT INTO process (
    id,
    name,
    type
) VALUES (
    'bellows',
    'Bellows',
    'crafting'
);

INSERT INTO process_input (
    process_id,
    resource_id
) VALUES
(
    'bellows',
    'leather'
),
(
    'bellows',
    'lumber'
);

INSERT INTO process_output (
    process_id,
    resource_id
) VALUES (
    'bellows',
    'bellows'
);

INSERT INTO technology_process_unlock (
    technology_id,
    process_id
) VALUES (
    'bellows',
    'bellows'
);