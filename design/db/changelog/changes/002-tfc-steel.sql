--liquibase formatted sql

--changeset tfc:002-tfc-steel

-- ============================================================================
-- Resources
-- ============================================================================
-- Only resources directly involved in the steel progression are introduced
-- here. Their own prerequisites will be added in later migrations.

INSERT INTO resource (
    id,
    name,
    category,
    tier,
    is_renewable,
    is_bootstrap,
    notes
) VALUES
    (
        'pig_iron_ingot',
        'Pig Iron Ingot',
        'metal',
        3,
        false,
        false,
        'Pig iron produced by a TFC blast furnace and cast into an ingot.'
    ),
    (
        'high_carbon_steel_ingot',
        'High Carbon Steel Ingot',
        'metal',
        3,
        false,
        false,
        'Intermediate ingot produced by working a Pig Iron Ingot on an anvil.'
    ),
    (
        'steel_ingot',
        'Steel Ingot',
        'metal',
        3,
        false,
        false,
        'Steel ingot produced by working a High Carbon Steel Ingot on an anvil.'
    ),
    (
        'iron_ore',
        'Iron Ore',
        'ore',
        3,
        false,
        false,
        'Iron-bearing input accepted by the blast furnace.'
    ),
    (
        'flux',
        'Flux',
        'material',
        3,
        false,
        false,
        'Flux consumed by the blast furnace when producing pig iron'
    ),
    (
        'charcoal',
        'Charcoal',
        'fuel',
        3,
        true,
        false,
        'Fuel consumed by the blast furnace.'
    );

-- ============================================================================
-- Sources
-- ============================================================================

INSERT INTO source (
    id,
    name,
    type,
    notes
) VALUES
    (
        'blast_furnace',
        'Blast Furnace',
        'process',
        'TFC blast furnace used to produce pig iron.'
    ),
    (
        'anvil_working',
        'Anvil Working',
        'process',
        'Working a metal ingot on an anvil.'
    );

-- ============================================================================
-- Processes
-- ============================================================================

INSERT INTO process (
    id,
    name,
    type,
    description,
    notes
) VALUES
    (
        'blast_furnace_pig_iron',
        'Blast Furnace: Pig Iron',
        'smelting',
        'Smelt iron-bearing material in a blast furnace to produce pig iron.',
        'This migration intentionally does not define the prerequisites for constructing or operating the blast furnace.'
    ),
    (
        'pig_iron_to_high_carbon_steel',
        'Forge Pig Iron into High Carbon Steel',
        'anvil',
        'Work a Pig Iron Ingot on an anvil to produce a High Carbon Steel Ingot.',
        'The required anvil technology is represented separately.'
    ),
    (
        'high_carbon_steel_to_steel',
        'Forge High Carbon Steel into Steel',
        'anvil',
        'Work a High Carbon Steel Ingot on an anvil to produce a Steel Ingot.',
        'The required anvil technology is represented separately.'
    );

-- ============================================================================
-- Process inputs
-- ============================================================================

INSERT INTO process_input (
    process_id,
    resource_id,
    quantity,
    consumed,
    notes
) VALUES
    (
        'blast_furnace_pig_iron',
        'iron_ore',
        1,
        true,
        'One unit represents an iron-bearing item accepted by the blast furnace; exact TFC quantities will be refined later.'
    ),
    (
        'blast_furnace_pig_iron',
        'flux',
        1,
        true,
        'Flux is consumed alongside the iron-bearing material.'
    ),
    (
        'blast_furnace_pig_iron',
        'charcoal',
        1,
        true,
        'Charcoal is consumed as blast furnace fuel.'
    ),
    (
        'pig_iron_to_high_carbon_steel',
        'pig_iron_ingot',
        1,
        true,
        NULL
    ),
    (
        'high_carbon_steel_to_steel',
        'high_carbon_steel_ingot',
        1,
        true,
        NULL
    );

-- ============================================================================
-- Process outputs
-- ============================================================================

INSERT INTO process_output (
    process_id,
    resource_id,
    quantity,
    chance,
    notes
) VALUES
    (
        'blast_furnace_pig_iron',
        'pig_iron_ingot',
        1,
        1.0,
        'Pig iron is cast into ingots after being produced by the blast furnace.'
    ),
    (
        'pig_iron_to_high_carbon_steel',
        'high_carbon_steel_ingot',
        1,
        1.0,
        NULL
    ),
    (
        'high_carbon_steel_to_steel',
        'steel_ingot',
        1,
        1.0,
        NULL
    );

-- ============================================================================
-- Technologies
-- ============================================================================

INSERT INTO technology (
    id,
    name,
    tier,
    description,
    notes
) VALUES
    (
        'blast_furnace',
        'Blast Furnace',
        3,
        'Advanced furnace capable of producing pig iron from iron-bearing material.',
        'Prerequisites for constructing the blast furnace will be added recursively later.'
    ),
    (
        'anvil',
        'Anvil',
        3,
        'Allows metal ingots to be worked into more advanced forms.',
        'The exact TFC anvil progression will be added later.'
    );

-- ============================================================================
-- Technology requirements
-- ============================================================================

INSERT INTO technology_resource_requirement (
    technology_id,
    resource_id,
    quantity,
    notes
) VALUES
    (
        'blast_furnace',
        'iron_ore',
        1,
        'The blast furnace requires iron-bearing material as input when producing pig iron.'
    ),
    (
        'blast_furnace',
        'flux',
        1,
        'Flux is required as a blast furnace input.'
    ),
    (
        'blast_furnace',
        'charcoal',
        1,
        'Charcoal is required as blast furnace fuel.'
    );

INSERT INTO technology_technology_requirement (
    technology_id,
    required_technology_id,
    notes
) VALUES
    (
        'blast_furnace',
        'anvil',
        'A tuyere used by the blast furnace is smithable on an anvil; this dependency will be refined when the blast furnace prerequisites are expanded.'
    );

-- ============================================================================
-- Process requirements
-- ============================================================================

INSERT INTO technology_process_unlock (
    technology_id,
    process_id,
    notes
) VALUES
    (
        'blast_furnace',
        'blast_furnace_pig_iron',
        'The blast furnace enables pig iron production.'
    ),
    (
        'anvil',
        'pig_iron_to_high_carbon_steel',
        'Anvil working converts pig iron into high carbon steel.'
    ),
    (
        'anvil',
        'high_carbon_steel_to_steel',
        'Anvil working converts high carbon steel into steel.'
    );

-- ============================================================================
-- Rollback
-- ============================================================================

--rollback DELETE FROM technology_process_unlock WHERE technology_id IN ('blast_furnace', 'anvil');
--rollback DELETE FROM technology_technology_requirement WHERE technology_id = 'blast_furnace';
--rollback DELETE FROM technology_resource_requirement WHERE technology_id = 'blast_furnace';
--rollback DELETE FROM technology WHERE id IN ('blast_furnace', 'anvil');
--rollback DELETE FROM process_output WHERE process_id IN ('blast_furnace_pig_iron', 'pig_iron_to_high_carbon_steel', 'high_carbon_steel_to_steel');
--rollback DELETE FROM process_input WHERE process_id IN ('blast_furnace_pig_iron', 'pig_iron_to_high_carbon_steel', 'high_carbon_steel_to_steel');
--rollback DELETE FROM process WHERE id IN ('blast_furnace_pig_iron', 'pig_iron_to_high_carbon_steel', 'high_carbon_steel_to_steel');
--rollback DELETE FROM source WHERE id IN ('blast_furnace', 'anvil_working');
--rollback DELETE FROM resource WHERE id IN ('pig_iron_ingot', 'high_carbon_steel_ingot', 'steel_ingot', 'iron_ore', 'flux', 'charcoal');

