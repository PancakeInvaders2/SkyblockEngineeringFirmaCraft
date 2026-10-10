
ServerEvents.afterRecipes(event => {
    const tags = {}

    Ingredient.all.itemIds.forEach(id => {
        const itemId = String(id)
        const item = Item.of(itemId)

        item.tags.forEach(tag => {
            const tagId = String(tag)

            if (!tags[tagId]) {
                tags[tagId] = []
            }

            tags[tagId].push(itemId)
        })
    })

    Object.keys(tags).forEach(tagId => {
        const parts = tagId.split(":")
        const namespace = parts[0]
        const path = parts[1]

        const pathParts = path.split("/")
        const filename = pathParts.pop()

        const directory = `kubejs/debug/tags/item/${namespace}`

        const filenamePath =
            pathParts.length > 0
                ? `${directory}/${pathParts.join("/")}/${filename}.json`
                : `${directory}/${filename}.json`

        JsonIO.write(filenamePath, tags[tagId])
    })

    console.info(
        `[TagDump] Wrote ${Object.keys(tags).length} item tags`
    )
})