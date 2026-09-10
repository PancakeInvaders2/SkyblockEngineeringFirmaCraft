--liquibase formatted sql

--changeset tfc:015-small-vessel-and-alloy-fuel

INSERT INTO resource (id, name, category)
VALUES
    ('small_vessel', 'Small Vessel', 'equipment');

INSERT INTO technology_resource_requirement (technology_id, resource_id)
VALUES
    ('alloying', 'small_vessel');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('copper_tin_to_bronze', 'charcoal'),
    ('copper_zinc_bismuth_to_bismuth_bronze', 'charcoal'),
    ('copper_silver_gold_to_black_bronze', 'charcoal'),
    ('copper_zinc_to_brass', 'charcoal'),
    ('copper_gold_to_rose_gold', 'charcoal'),
    ('copper_silver_to_sterling_silver', 'charcoal');