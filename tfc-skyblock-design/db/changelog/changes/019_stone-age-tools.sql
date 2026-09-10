--liquibase formatted sql

--changeset tfc:019-stone-age-tools

INSERT INTO resource (id, name, category)
VALUES
    ('loose_rock', 'Loose Rock', 'mineral'),
    ('knife', 'Knife', 'tool'),
    ('axe', 'Axe', 'tool'),
    ('shovel', 'Shovel', 'tool'),
    ('hoe', 'Hoe', 'tool');

INSERT INTO technology (id, name)
VALUES
    ('knapping', 'Knapping');

INSERT INTO process (id, name, type)
VALUES
    ('knap_knife', 'Knapping Knife', 'knapping'),
    ('knap_axe', 'Knapping Axe', 'knapping'),
    ('knap_shovel', 'Knapping Shovel', 'knapping'),
    ('knap_hoe', 'Knapping Hoe', 'knapping'),
    ('knap_hammer', 'Knapping Hammer', 'knapping');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('knap_knife', 'loose_rock'),
    ('knap_knife', 'stick'),

    ('knap_axe', 'loose_rock'),
    ('knap_axe', 'stick'),

    ('knap_shovel', 'loose_rock'),
    ('knap_shovel', 'stick'),

    ('knap_hoe', 'loose_rock'),
    ('knap_hoe', 'stick'),

    ('knap_hammer', 'loose_rock'),
    ('knap_hammer', 'stick');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('knap_knife', 'knife'),
    ('knap_axe', 'axe'),
    ('knap_shovel', 'shovel'),
    ('knap_hoe', 'hoe'),
    ('knap_hammer', 'hammer');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('knapping', 'knap_knife'),
    ('knapping', 'knap_axe'),
    ('knapping', 'knap_shovel'),
    ('knapping', 'knap_hoe'),
    ('knapping', 'knap_hammer');

