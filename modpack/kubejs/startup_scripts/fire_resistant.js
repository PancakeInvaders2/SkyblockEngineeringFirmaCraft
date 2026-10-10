ItemEvents.modification(event => {

    const cobblestones = []

    for (const itemId of Item.getTypeList()) {

        if (!String(itemId).includes('/cobble/')) {
            continue
        }

        if ((String(itemId).split('/cobble/')[1]).includes('_')) {
            continue
        }

        cobblestones.push(String(itemId))
    }

    console.info(
        `[Fire Resistant] cobblestones: ${cobblestones}`
    )

    event.modify(cobblestones, item => {
        item.setFireResistant()
    })
})