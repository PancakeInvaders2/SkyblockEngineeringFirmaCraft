const ChunkData = Java.loadClass('net.dries007.tfc.world.chunkdata.ChunkData')
const BuiltInRegistries = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
const ResourceLocation = Java.loadClass('net.minecraft.resources.ResourceLocation')

const hardened_igneous_rocks = [
    "tfc:rock/hardened/granite",
    "tfc:rock/hardened/diorite",
    "tfc:rock/hardened/gabbro",
    "tfc:rock/hardened/rhyolite",
    "tfc:rock/hardened/basalt",
    "tfc:rock/hardened/andesite",
    "tfc:rock/hardened/dacite",
]

NativeEvents.onEvent("net.neoforged.neoforge.event.level.BlockEvent$FluidPlaceBlockEvent", event => {
    

    
    if (event.getLevel() && event.getLevel().isClientSide()) {
        return
    }

    let id = event.getNewState().getBlock().toString()
    if (id != "Block{tfc:rock/hardened/rhyolite}" && id != "Block{tfc:rock/hardened/gabbro}") {
        return
    }

    let chunkGenerator = event.getLevel().getChunkSource().getGenerator()

    try {
        let chunkData = ChunkData.get(event.getLevel().getChunk(event.getPos()))

        let foundTheLocalIgneousRock = false
        let localIgneousRock = null
        let defaultRockAtThePosition = null
        let blockPos = event.getPos();

        // if there's a igneous rock in the area, select that rock
        while(!foundTheLocalIgneousRock && blockPos.getY() > -64){ 
            defaultRockAtThePosition = chunkData.getRockData().getRock(blockPos).hardened().defaultBlockState()
            console.info(`[StoneGeneration] defaultRockAtThePosition: ${defaultRockAtThePosition} for ${blockPos}`)
            if(hardened_igneous_rocks.includes(defaultRockAtThePosition.toString())){
                foundTheLocalIgneousRock = true
                localIgneousRock = defaultRockAtThePosition;
                event.setNewState(localIgneousRock)
                return
            }
            blockPos = blockPos.below()
        }

        // no igneous rock has been found at the bottom of the world, 
        // meaning that in this area it's metamorphic rocks at the bottom (such as gneiss or schist)
        // since we don't want lava-water interactions to output rocks that are not igneous, 
        // we'll just do rhyolite above Y=0, and granite below that
        if(event.getPos().getY() > 0){
            event.setNewState(
                BuiltInRegistries.BLOCK
                    .get(ResourceLocation.parse("tfc:rock/hardened/rhyolite"))
                    .defaultBlockState())
        }
        else{
            event.setNewState(
                BuiltInRegistries.BLOCK
                    .get(ResourceLocation.parse("tfc:rock/hardened/granite"))
                    .defaultBlockState())
        }


    } 
    catch (_) { return }

})


