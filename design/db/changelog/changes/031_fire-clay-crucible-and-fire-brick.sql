--liquibase formatted sql
--changeset tfc:031-fire-clay-crucible-and-fire-brick

INSERT INTO resource (id, name, category)
VALUES
    ('kaolinite_powder', 'Kaolinite Powder', 'mineral'),
    ('graphite_powder', 'Graphite Powder', 'mineral'),
    ('fire_clay', 'Fire Clay', 'pottery');

INSERT INTO process (id, name, type)
VALUES
    ('craft_fire_clay', 'Craft Fire Clay', 'crafting'),
    ('craft_crucible', 'Craft Crucible', 'crafting'),
    ('craft_fire_brick', 'Craft Fire Brick', 'crafting');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_fire_clay', 'kaolinite_powder'),
    ('craft_fire_clay', 'graphite_powder'),
    ('craft_fire_clay', 'clay'),
    ('craft_crucible', 'fire_clay'),
    ('craft_fire_brick', 'fire_clay');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_fire_clay', 'fire_clay'),
    ('craft_crucible', 'crucible'),
    ('craft_fire_brick', 'fire_brick');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('pottery', 'craft_fire_clay'),
    ('pottery', 'craft_crucible'),
    ('pottery', 'craft_fire_brick');