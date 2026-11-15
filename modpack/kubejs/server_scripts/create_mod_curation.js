ServerEvents.recipes(event => {

    console.info('[CreateCuration] Removing all Create recipes')

    event.remove({ mod: 'create' })
})


RecipeViewerEvents.removeEntriesCompletely('item', event => {
    event.remove('@create')

})

RecipeViewerEvents.addEntries('item', event => {

    // player starts with some tree fertilizer to make the early game less boring
    event.add('create:tree_fertilizer')

    // redstone components
    event.add('create:linked_controller')
    event.add('create:redstone_link')
    event.add('create:analog_lever')
    event.add('create:pulse_extender')
    event.add('create:pulse_repeater')
    event.add('create:pulse_timer')
    event.add('create:powered_latch')
    event.add('create:powered_toggle_latch')

    // goggles and wrench are sometimes used to configure/inspect the redstone component
    event.add('create:goggles')
    event.add('create:wrench')

    // schematics
    event.add('create:schematicannon')
    event.add('create:schematic_table')
    event.add('create:schematic')
    event.add('create:schematic_and_quill')
    event.add('create:empty_schematic')

    // components I might want to use in recipes
    // event.add('create:transmitter')
    // event.add('create:electron_tube')

    // useful storage things
    event.add('create:chute')
    event.add('create:item_vault')


})




    
