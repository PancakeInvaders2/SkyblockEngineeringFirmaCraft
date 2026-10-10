--liquibase formatted sql

--changeset tfc:020-firepit-and-ceramic-pot

INSERT INTO technology (id, name)
VALUES
    ('firepit', 'Firepit');


INSERT INTO resource (id, name, category)
VALUES
    ('firepit', 'Firepit', 'equipment'),
    ('ceramic_pot', 'Ceramic Pot', 'pottery');

INSERT INTO technology_resource_requirement (technology_id, resource_id)
VALUES
    ('firepit', 'firepit');

INSERT INTO process (id, name, type)
VALUES
    ('craft_firepit', 'Craft Firepit', 'craft'),
    ('cook_ceramic_pot', 'Cook Ceramic Pot', 'firing');


INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_firepit', 'wooden_log'),
    ('craft_firepit', 'stick'),
    ('craft_firepit', 'straw'),
    ('cook_ceramic_pot', 'clay');


INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_firepit', 'firepit'),
    ('cook_ceramic_pot', 'ceramic_pot');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('fire_starter', 'craft_firepit'),
    ('pottery', 'cook_ceramic_pot');
