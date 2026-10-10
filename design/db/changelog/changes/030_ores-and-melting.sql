--liquibase formatted sql

--changeset tfc:030-ores-and-melting

INSERT INTO resource (id, name, category)
VALUES
    ('copper_ore', 'Copper Ore', 'ore'),
    ('gold_ore', 'Gold Ore', 'ore'),
    ('silver_ore', 'Silver Ore', 'ore'),
    ('tin_ore', 'Tin Ore', 'ore'),
    ('bismuth_ore', 'Bismuth Ore', 'ore'),
    ('zinc_ore', 'Zinc Ore', 'ore'),
    ('nickel_ore', 'Nickel Ore', 'ore');

INSERT INTO technology (id, name)
VALUES
    ('melting', 'Melting');

INSERT INTO technology_resource_requirement (technology_id, resource_id)
VALUES
    ('melting', 'small_vessel');

INSERT INTO process (id, name, type)
VALUES
    ('melt_copper_ore', 'Melt Copper Ore', 'melting'),
    ('melt_gold_ore', 'Melt Gold Ore', 'melting'),
    ('melt_silver_ore', 'Melt Silver Ore', 'melting'),
    ('melt_tin_ore', 'Melt Tin Ore', 'melting'),
    ('melt_bismuth_ore', 'Melt Bismuth Ore', 'melting'),
    ('melt_zinc_ore', 'Melt Zinc Ore', 'melting'),
    ('melt_nickel_ore', 'Melt Nickel Ore', 'melting'),
    ('melt_iron_ore', 'Melt Iron Ore', 'melting');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('melt_copper_ore', 'copper_ore'),
    ('melt_copper_ore', 'charcoal'),
    ('melt_gold_ore', 'gold_ore'),
    ('melt_gold_ore', 'charcoal'),
    ('melt_silver_ore', 'silver_ore'),
    ('melt_silver_ore', 'charcoal'),
    ('melt_tin_ore', 'tin_ore'),
    ('melt_tin_ore', 'charcoal'),
    ('melt_bismuth_ore', 'bismuth_ore'),
    ('melt_bismuth_ore', 'charcoal'),
    ('melt_zinc_ore', 'zinc_ore'),
    ('melt_zinc_ore', 'charcoal'),
    ('melt_nickel_ore', 'nickel_ore'),
    ('melt_nickel_ore', 'charcoal'),
    ('melt_iron_ore', 'iron_ore'),
    ('melt_iron_ore', 'charcoal');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('melt_copper_ore', 'copper_ingot'),
    ('melt_gold_ore', 'gold_ingot'),
    ('melt_silver_ore', 'silver_ingot'),
    ('melt_tin_ore', 'tin_ingot'),
    ('melt_bismuth_ore', 'bismuth_ingot'),
    ('melt_zinc_ore', 'zinc_ingot'),
    ('melt_nickel_ore', 'nickel_ingot'),
    ('melt_iron_ore', 'cast_iron_ingot');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('melting', 'melt_copper_ore'),
    ('melting', 'melt_gold_ore'),
    ('melting', 'melt_silver_ore'),
    ('melting', 'melt_tin_ore'),
    ('melting', 'melt_bismuth_ore'),
    ('melting', 'melt_zinc_ore'),
    ('melting', 'melt_nickel_ore'),
    ('melting', 'melt_iron_ore');
