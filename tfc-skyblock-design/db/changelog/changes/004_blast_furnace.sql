--liquibase formatted sql

--changeset tfc:004-blast-furnace-requirements

-- ============================================================================
-- Resources required to obtain / operate the Blast Furnace
-- ============================================================================
-- We deliberately do not define how these resources are obtained yet.
-- That will be handled by subsequent backward-expansion changesets.

INSERT INTO resource (
    id,
    name,
    category,
    is_renewable,
    is_bootstrap,
    notes
) VALUES
    (
        'crucible',
        'Crucible',
        'equipment',
        false,
        false,
        'Metal fluid container used to collect molten metal from the blast furnace.'
    ),
    (
        'wrought_iron_sheet',
        'Wrought Iron Sheet',
        'metal',
        false,
        false,
        'Used to construct and reinforce the blast furnace chimney.'
    ),
    (
        'fire_brick',
        'Fire Brick',
        'building_material',
        false,
        false,
        'Used to construct the blast furnace chimney. Each chimney layer requires four Fire Bricks.'
    ),
    (
        'tuyere',
        'Tuyere',
        'equipment',
        false,
        false,
        'Metal pipe placed in the blast furnace to provide the airflow required for steel production.'
    ),
    (
        'bellows',
        'Bellows',
        'equipment',
        false,
        false,
        'Provides additional airflow to the blast furnace so it can reach the temperature required to melt iron.'
    ),
    (
        'fire_starter',
        'Fire Starter',
        'tool',
        false,
        false,
        'Used to ignite the blast furnace. Flint and Steel is an alternative ignition method.'
    );

-- ============================================================================
-- Blast Furnace acquisition requirements
-- ============================================================================
-- These describe what is needed to obtain/use the Blast Furnace itself.
-- They are intentionally separate from the inputs of the
-- blast_furnace_pig_iron process.

INSERT INTO technology_resource_requirement (
    technology_id,
    resource_id,
    quantity,
    notes
) VALUES
    (
        'blast_furnace',
        'crucible',
        1,
        'Required to craft the blast furnace and to collect its molten output.'
    ),
    (
        'blast_furnace',
        'wrought_iron_sheet',
        1,
        'Required to construct the blast furnace and line its chimney. A complete chimney requires multiple sheets.'
    ),
    (
        'blast_furnace',
        'fire_brick',
        1,
        'Required to construct the blast furnace chimney. Each chimney layer requires four Fire Bricks.'
    ),
    (
        'blast_furnace',
        'tuyere',
        1,
        'Required in the blast furnace interface to provide the airflow needed for steel production.'
    ),
    (
        'blast_furnace',
        'bellows',
        1,
        'Required to provide additional airflow and reach the temperature needed to melt iron.'
    ),
    (
        'blast_furnace',
        'fire_starter',
        1,
        'Required to ignite the blast furnace. Flint and Steel is an alternative.'
    );

-- ============================================================================
-- Remove process inputs that were incorrectly modeled as technology
-- acquisition requirements in the previous changeset.
-- ============================================================================

DELETE FROM technology_resource_requirement
WHERE technology_id = 'blast_furnace'
  AND resource_id IN (
      'iron_ore',
      'flux',
      'charcoal'
  );

-- ============================================================================
-- Rollback
-- ============================================================================

--rollback INSERT INTO technology_resource_requirement (technology_id, resource_id, quantity, notes)
--rollback VALUES
--rollback     ('blast_furnace', 'iron_ore', 1, 'Blast furnace input.'),
--rollback     ('blast_furnace', 'flux', 1, 'Blast furnace input.'),
--rollback     ('blast_furnace', 'charcoal', 1, 'Blast furnace fuel.');

--rollback DELETE FROM technology_resource_requirement
--rollback WHERE technology_id = 'blast_furnace'
--rollback   AND resource_id IN (
--rollback       'crucible',
--rollback       'wrought_iron_sheet',
--rollback       'fire_brick',
--rollback       'tuyere',
--rollback       'bellows',
--rollback       'fire_starter'
--rollback   );

--rollback DELETE FROM resource
--rollback WHERE id IN (
--rollback     'crucible',
--rollback     'wrought_iron_sheet',
--rollback     'fire_brick',
--rollback     'tuyere',
--rollback     'bellows',
--rollback     'fire_starter'
--rollback );
