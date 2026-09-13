--liquibase formatted sql

--changeset tfc:021-cook-liquids-and-lye

INSERT INTO resource (id, name, category)
VALUES
    ('ash', 'Ash', 'material'),
    ('lye', 'Lye', 'material');

INSERT INTO technology (id, name)
VALUES
    ('cook_liquids', 'Cook Liquids');

INSERT INTO technology_resource_requirement (technology_id, resource_id)
VALUES
    ('cook_liquids', 'firepit'),
    ('cook_liquids', 'ceramic_pot');

INSERT INTO process (id, name, type)
VALUES
    ('make_lye', 'Make Lye', 'cooking');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('make_lye', 'ash');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('make_lye', 'lye');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('cook_liquids', 'make_lye');
