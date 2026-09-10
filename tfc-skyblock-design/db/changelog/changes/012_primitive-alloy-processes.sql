--liquibase formatted sql

--changeset tfc:012-primitive-alloy-processes

INSERT INTO process (
    id,
    name,
    type
) VALUES
(
    'copper_tin_to_bronze',
    'Alloy Copper and Tin into Bronze',
    'alloying'
),
(
    'copper_zinc_bismuth_to_bismuth_bronze',
    'Alloy Copper, Zinc, and Bismuth into Bismuth Bronze',
    'alloying'
),
(
    'copper_silver_gold_to_black_bronze',
    'Alloy Copper, Silver, and Gold into Black Bronze',
    'alloying'
),
(
    'copper_zinc_to_brass',
    'Alloy Copper and Zinc into Brass',
    'alloying'
),
(
    'copper_gold_to_rose_gold',
    'Alloy Copper and Gold into Rose Gold',
    'alloying'
),
(
    'copper_silver_to_sterling_silver',
    'Alloy Copper and Silver into Sterling Silver',
    'alloying'
);

INSERT INTO process_input (
    process_id,
    resource_id
) VALUES
(
    'copper_tin_to_bronze',
    'copper_ingot'
),
(
    'copper_tin_to_bronze',
    'tin_ingot'
),
(
    'copper_zinc_bismuth_to_bismuth_bronze',
    'copper_ingot'
),
(
    'copper_zinc_bismuth_to_bismuth_bronze',
    'zinc_ingot'
),
(
    'copper_zinc_bismuth_to_bismuth_bronze',
    'bismuth_ingot'
),
(
    'copper_silver_gold_to_black_bronze',
    'copper_ingot'
),
(
    'copper_silver_gold_to_black_bronze',
    'silver_ingot'
),
(
    'copper_silver_gold_to_black_bronze',
    'gold_ingot'
),
(
    'copper_zinc_to_brass',
    'copper_ingot'
),
(
    'copper_zinc_to_brass',
    'zinc_ingot'
),
(
    'copper_gold_to_rose_gold',
    'copper_ingot'
),
(
    'copper_gold_to_rose_gold',
    'gold_ingot'
),
(
    'copper_silver_to_sterling_silver',
    'copper_ingot'
),
(
    'copper_silver_to_sterling_silver',
    'silver_ingot'
);

INSERT INTO process_output (
    process_id,
    resource_id
) VALUES
(
    'copper_tin_to_bronze',
    'bronze_ingot'
),
(
    'copper_zinc_bismuth_to_bismuth_bronze',
    'bismuth_bronze_ingot'
),
(
    'copper_silver_gold_to_black_bronze',
    'black_bronze_ingot'
),
(
    'copper_zinc_to_brass',
    'brass_ingot'
),
(
    'copper_gold_to_rose_gold',
    'rose_gold_ingot'
),
(
    'copper_silver_to_sterling_silver',
    'sterling_silver_ingot'
);