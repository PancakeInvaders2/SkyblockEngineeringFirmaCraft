

LootJS.lootTables(event => {

    // mob backpack content
    const backpackZombie = event.create("skyblock:backpacks/zombie")
    backpackZombie.createPool(pool => { pool.addEntry( LootEntry.of("minecraft:rotten_flesh").setCount([5,7]) ) }); 
    backpackZombie.createPool(pool => { pool.addEntry( LootEntry.of("minecraft:bamboo").setCount([0,1]) ) });
    const seeds = Ingredient.of("#c:seeds")
        .getItemIds()
        .filter(id => !id.startsWith("minecraft:"));
    backpackZombie.createPool(pool => {
        pool.rolls([3]);
        seeds.forEach(id => {
            pool.addEntry(LootEntry.of(id).withWeight(1).setCount([1]));
        });
    });
 
    
    // Drowned: drop copper and ocean things
    const drowned = event.getEntityTable("minecraft:drowned");
    drowned.createPool(pool => {
        // represents river/lake drowned
        // half the time, 4 on average -> 2 deposits per drowned on average
        // each deposit has ~66% chance of giving a small copper, so 7.5 drowned for an ingot on average for a toold
        pool.addEntry( LootEntry.of("tfc:deposit/native_copper/shale").killedByPlayer().withWeight(4).setCount([3]))

        // represents ocean drowned
        pool.addEntry( LootEntry.of("tfc:powder/salt").killedByPlayer().withWeight(2).setCount([1]))
        pool.addEntry( LootEntry.empty().withWeight(2))
        
    });

    // Creepers: sulfur instead of gunpowder
    const creeper = event.getEntityTable("minecraft:creeper");
    creeper.firstPool().modifyItemEntry(itemEntry => {
        if (itemEntry.item.id === "minecraft:gunpowder") {
            itemEntry.setItem("tfc:ore/sulfur")
        }
        return itemEntry
    })

    // Bats drop small hide
    const bat = event.getEntityTable("minecraft:bat");
    bat.createPool(pool => {
        // one small hide every ~5 bats seems reasonable
        pool.addEntry( LootEntry.of("tfc:small_raw_hide").killedByPlayer().withWeight(1).setCount(1))
        pool.addEntry( LootEntry.empty().withWeight(4))
    });

});




