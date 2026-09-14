LootJS.lootTables(event => {
    function dumpEntry(entry) {
        const result = {
            type: String(entry.getType())
        }

        if (entry.isItem()) {
            result.kind = "item"
            result.item = String(entry.getItem())
        } else if (entry.isComposite()) {
            result.kind = "composite"
            result.entries = []

            entry.getEntries().forEach(child => {
                result.entries.push(dumpEntry(child))
            })
        } else if (entry.isDynamic()) {
            result.kind = "dynamic"
        } else if (entry.isReference()) {
            result.kind = "reference"
        } else if (entry.isTag()) {
            result.kind = "tag"
        } else {
            result.kind = "other"
        }

        if (typeof entry.getWeight === "function") {
            result.weight = entry.getWeight()
            result.quality = entry.getQuality()
        }

        return result
    }

    event.forEachTable(table => {
        const id = String(table.getLocation())

        // minecraft:blocks/plant/foo
        const parts = id.split(":")

        const namespace = parts[0]
        const path = parts[1]

        const pathParts = path.split("/")

        // Remove the filename from the directory path
        const filename = pathParts.pop()

        const tableDump = {
            lootType: String(table.getLootType()),
            pools: []
        }

        table.getPools().forEach(pool => {
            const poolDump = {
                name: pool.getName(),
                entries: []
            }

            pool.getEntries().forEach(entry => {
                poolDump.entries.push(dumpEntry(entry))
            })

            tableDump.pools.push(poolDump)
        })

        // namespace + all path components except filename
        const directory = `kubejs/debug/loot_tables/${namespace}`

        const filenamePath =
            pathParts.length > 0
                ? `${directory}/${pathParts.join("/")}/${filename}.json`
                : `${directory}/${filename}.json`

        JsonIO.write(filenamePath, tableDump)
    })

    console.info("[LootDump] Finished writing loot tables")
})