ServerEvents.recipes(event => {
    let count = 0

    event.forEachRecipe({}, recipe => {
        const id = String(recipe.getId())

        const parts = id.split(":")
        const namespace = parts[0]
        const path = parts[1]

        const pathParts = path.split("/")
        const filename = pathParts.pop()

        const directory = `kubejs/debug/recipes/${namespace}`

        const filenamePath =
            pathParts.length > 0
                ? `${directory}/${pathParts.join("/")}/${filename}.json`
                : `${directory}/${filename}.json`

        const json = JSON.parse(String(recipe.json))

        JsonIO.write(filenamePath, json)

        count++
    })

    console.info(`[RecipeDump] Wrote ${count} recipes`)
})