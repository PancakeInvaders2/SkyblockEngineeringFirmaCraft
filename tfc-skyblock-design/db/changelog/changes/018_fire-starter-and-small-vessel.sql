--liquibase formatted sql

--changeset tfc:018-fire-starter-and-small-vessel

INSERT INTO technology (id, name)
VALUES
    ('fire_starter', 'Fire Starter');

INSERT INTO process (id, name, type)
VALUES
    ('craft_fire_starter', 'Craft Fire Starter', 'crafting'),
    ('cook_small_vessel', 'Cook Small Vessel', 'firing');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_fire_starter', 'stick'),

    ('cook_small_vessel', 'clay');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_fire_starter', 'fire_starter'),
    ('cook_small_vessel', 'small_vessel');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('fire_starter', 'craft_fire_starter'),
    ('pottery', 'cook_small_vessel');
