--liquibase formatted sql

--changeset tfc:007-bellows

-- wether something is renewable is not an intrinsic characteristic of the resource, but a result of the relationships
ALTER TABLE resource
    DROP COLUMN is_renewable;

-- these notes are never going to accurately represent the graph since it rapidly changes, might as well not have them
ALTER TABLE technology_resource_requirement
    DROP COLUMN notes;

    -- these notes are never going to accurately represent the graph since it rapidly changes, might as well not have them
ALTER TABLE resource
    DROP COLUMN notes;


INSERT INTO resource (
    id,
    name,
    category
) VALUES
    (
        'leather',
        'Leather',
        'Misc'
    ),
    (
        'lumber',
        'Lumber',
        'Wood'
    );

INSERT INTO technology (
    id,
    name,
    notes
) VALUES (
    'bellows',
    'Bellows',
    'Increase airflow through compatible heating devices.'
);

INSERT INTO technology_resource_requirement (
    technology_id,
    resource_id
) VALUES (
    'bellows',
    'leather'
),
(
    'bellows',
    'lumber'
);

