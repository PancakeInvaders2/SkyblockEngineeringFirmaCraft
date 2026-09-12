--liquibase formatted sql
--changeset tfc:036-vanilla-tfc-scenario

INSERT INTO scenario (id, name)
VALUES
    ('vanilla_tfc', 'Vanilla TFC');

INSERT INTO scenario_resource (scenario_id, resource_id)
VALUES
    ('vanilla_tfc', 'animal'),
    ('vanilla_tfc', 'bismuth_ore'),
    ('vanilla_tfc', 'clay'),
    ('vanilla_tfc', 'copper_ore'),
    ('vanilla_tfc', 'flux'),
    ('vanilla_tfc', 'gold_ore'),
    ('vanilla_tfc', 'graphite'),
    ('vanilla_tfc', 'iron_ore'),
    ('vanilla_tfc', 'kaolinite'),
    ('vanilla_tfc', 'loose_rock'),
    ('vanilla_tfc', 'nickel_ore'),
    ('vanilla_tfc', 'silver_ore'),
    ('vanilla_tfc', 'stick'),
    ('vanilla_tfc', 'straw'),
    ('vanilla_tfc', 'tin_ore'),
    ('vanilla_tfc', 'tree'),
    ('vanilla_tfc', 'zinc_ore');