// throwing a cobblestone into a flowing lava makes a raw conglomerate
// yes, conglomerate is usually a sedimentary rocks. But wikipedia says
// "Les conglomérats sont le plus souvent de nature sédimentaire, mais ils peuvent également être volcaniques."


const COBBLESTONE_TAG = 'c:cobblestones'
const CONGLOMERATE = 'tfc:rock/raw/conglomerate'

ServerEvents.tick(event => {

    for (const level of event.server.getAllLevels()) {

        for (const entity of level.getEntities()) {

            if (!entity.getType().toString().contains('item')) {
                continue
            }

            const stack = entity.getItem()

            if (stack.isEmpty() || !stack.hasTag(COBBLESTONE_TAG)) {
                continue
            }

            const pos = BlockPos.containing(entity.position())

            const positions = [
                pos,
                pos.above(),
                pos.below(),
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west()
            ]

            for (const checkPos of positions) {

                const fluidId =
                    level.getFluidState(checkPos).getType().toString()

                if (fluidId !== 'minecraft:flowing_lava') {
                    continue
                }

                console.info(
                    `[Conglomerate] Converting cobblestone ` +
                    `into raw conglomerate at ${checkPos}`
                )

                level.setBlock(
                    checkPos,
                    Block.getBlock(CONGLOMERATE).defaultBlockState(),
                    3
                )

                entity.discard()

                return
            }
        }
    }
})