ServerEvents.afterRecipes(event => {

    console.info("[FluidTagDump] In fluid tag dump afterRecipes")

    const fluidTags = {}

    Fluid.getTypes().forEach(id => {
        const fluidId = String(id)
        const fluid = Fluid.getType(id)
        const holder = fluid.asHolder()

        const iterator = holder.tags().iterator()

        while (iterator.hasNext()) {
            const tag = iterator.next()
            const tagId = String(tag.location())

            if (!fluidTags[tagId]) {
                fluidTags[tagId] = []
            }

            fluidTags[tagId].push(fluidId)
        }
    })

    Object.keys(fluidTags).forEach(tagId => {
        const parts = tagId.split(":")
        const namespace = parts[0]
        const path = parts[1]

        const pathParts = path.split("/")
        const filename = pathParts.pop()

        const directory = `kubejs/debug/tags/fluid/${namespace}`

        const filenamePath =
            pathParts.length > 0
                ? `${directory}/${pathParts.join("/")}/${filename}.json`
                : `${directory}/${filename}.json`

        JsonIO.write(filenamePath, fluidTags[tagId])
    })

    console.info(
        `[FluidTagDump] Wrote ${Object.keys(fluidTags).length} fluid tags`
    )
})