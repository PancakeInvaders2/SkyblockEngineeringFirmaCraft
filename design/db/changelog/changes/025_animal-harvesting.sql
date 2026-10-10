--liquibase formatted sql

--changeset tfc:025-animal-harvesting

INSERT INTO resource (id, name, category)
VALUES
    ('meat', 'Meat', 'animal_products'),
    ('raw_hide', 'Raw Hide', 'animal_products'),
    ('wool', 'Wool', 'animal_products'),
    ('bone', 'Bone', 'animal_products'),
    ('bladder', 'Bladder', 'animal_products'),
    ('animal', 'Animal', 'entity');

INSERT INTO process (id, name, type)
VALUES
    ('kill_animal', 'Kill Animal', 'combat');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('kill_animal', 'animal');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('kill_animal', 'meat'),
    ('kill_animal', 'raw_hide'),
    ('kill_animal', 'wool'),
    ('kill_animal', 'bone'),
    ('kill_animal', 'bladder');
