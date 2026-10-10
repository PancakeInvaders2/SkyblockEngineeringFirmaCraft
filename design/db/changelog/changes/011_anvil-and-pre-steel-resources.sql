--liquibase formatted sql

--changeset tfc:011-anvil-and-pre-steel-resources

ALTER TABLE technology_process_unlock
    DROP COLUMN notes;


INSERT INTO resource (
    id,
    name,
    category
) VALUES
(
    'hammer',
    'Hammer',
    'tool'
),
(
    'raw_rock',
    'Raw Rock',
    'material'
),
(
    'copper_ingot',
    'Copper Ingot',
    'metal'
),
(
    'gold_ingot',
    'Gold Ingot',
    'metal'
),
(
    'cast_iron_ingot',
    'Cast Iron Ingot',
    'metal'
),
(
    'silver_ingot',
    'Silver Ingot',
    'metal'
),
(
    'tin_ingot',
    'Tin Ingot',
    'metal'
),
(
    'bismuth_ingot',
    'Bismuth Ingot',
    'metal'
),
(
    'nickel_ingot',
    'Nickel Ingot',
    'metal'
),
(
    'zinc_ingot',
    'Zinc Ingot',
    'metal'
),
(
    'bronze_ingot',
    'Bronze Ingot',
    'metal'
),
(
    'bismuth_bronze_ingot',
    'Bismuth Bronze Ingot',
    'metal'
),
(
    'black_bronze_ingot',
    'Black Bronze Ingot',
    'metal'
),
(
    'brass_ingot',
    'Brass Ingot',
    'metal'
),
(
    'rose_gold_ingot',
    'Rose Gold Ingot',
    'metal'
),
(
    'sterling_silver_ingot',
    'Sterling Silver Ingot',
    'metal'
);

INSERT INTO technology_resource_requirement (
    technology_id,
    resource_id
) VALUES
(
    'anvil',
    'hammer'
),
(
    'anvil',
    'raw_rock'
);
