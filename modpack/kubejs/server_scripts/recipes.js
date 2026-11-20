//const { input } = require("@package/net/dries007/tfc/common/recipes");

ServerEvents.recipes(event => {

    // brown, white, black, red, yellow, green, pink
    const rocks = [
        { type: "marble", color: "white" },
        { type: "chalk", color: "white" },
        { type: "dacite", color: "white" },
        { type: "diorite", color: "white" },
        { type: "rhyolite", color: "white" },
        { type: "quartzite", color: "pink" },
        { type: "granite", color: "pink" },
        { type: "schist", color: "green" },
        { type: "conglomerate", color: "brown" },
        { type: "gabbro", color: "brown" },
        { type: "gneiss", color: "brown" },
        { type: "slate", color: "brown" },
        { type: "limestone", color: "yellow" },
        { type: "andesite", color: "black" },
        { type: "basalt", color: "black" },
        { type: "dolomite", color: "black" },
        { type: "shale", color: "black" },
        { type: "tuff", color: "black" },
        { type: "chert", color: "red" },
        { type: "claystone", color: "red" },
        { type: "phyllite", color: "red" }
    ];

    const sandColors = [
        "white",
        "pink",
        "green",
        "brown",
        "yellow",
        "black",
        "red",
    ]

    function quern(recipePath, inputItem, outputCount, outputItem ){
        // TFC quern
        event.custom({
            type: "tfc:quern",
            ingredient: [{ item: inputItem }],
            result: { count: outputCount, id: outputItem }
        }).id(`kubejs:quern/${recipePath}`);
    }
    function crusher(recipePath, inputItem, outputCount1, outputItem1, outputCount2, outputItem2) {
        const recipe = {
            type: "immersiveengineering:crusher",
            energy: 1600,
            input: { item: inputItem },
            result: { count: outputCount1, id: outputItem1 }
        };

        if (outputItem2 !== undefined && outputCount2 > 0) {
            recipe.secondaries = [{
                count: outputCount2,
                output: { item: outputItem2 }
            }];
        }

        event.custom(recipe).id(`kubejs:crusher/${recipePath}`);
    }
    function pulverizer(recipePath, inputItem, outputs) {
        event.custom({
            type: "oritech:pulverizer",
            ingredients: [{ item: inputItem }],
            results: outputs.map(output => ({
                count: output.count,
                id: output.item
            })),
            time: 120
        }).id(`kubejs:pulverizer/${recipePath}`);
    }
    function grinder(recipePath, inputItem, outputs) {
        event.custom({
            type: "oritech:grinder",
            ingredients: [{ item: inputItem }],
            results: outputs.map(output => ({
                count: output.count,
                id: output.item
            })),
            time: 120
        }).id(`kubejs:grinder/${recipePath}`);
    }
    function generalCrushing(recipePath, inputItem, outputCount, outputItem) {
        quern(recipePath, inputItem, outputCount, outputItem )
        crusher(recipePath, inputItem, outputCount, outputItem )
        pulverizer(recipePath, inputItem, [{count: outputCount, item: outputItem}] )
    }


    // sticks from arundo
    event.shaped(
        Item.of('minecraft:stick', 1),
        [
            'A',
            'A'
        ],
        {
            A: 'tfc:plant/arundo'
        }
    )


    // straw from dry phragmite
    event.shaped(
        Item.of('tfc:straw', 1),
        [
            'A',
            'A'
        ],
        {
            A: 'tfc:plant/dry_phragmite'
        }
    )


    // chiseling hardened stone into raw stone
    for (const rock of rocks) {
        var recipeId = `tfc:chisel/rock/smooth/${rock.type}`;

        // Replace the original smoothing recipe so hardened rock is no longer an input.
        event.remove({ id: recipeId });
        event.custom({
            type: "tfc:chisel",
            ingredient: [`tfc:rock/raw/${rock.type}`],
            mode: "tfc:smooth",
            result: `tfc:rock/smooth/${rock.type}`
        }).id(recipeId);

        // Add a recipe to turn hardened rock back into raw rock.
        event.custom({
            type: "tfc:chisel",
            ingredient: [`tfc:rock/hardened/${rock.type}`],
            mode: "tfc:smooth",
            result: `tfc:rock/raw/${rock.type}`
        }).id(`kubejs:chisel/rock/hardened_to_raw/${rock.type}`);
    }



    // cobblestone -> gravel
    for (const rock of rocks) {
        generalCrushing(`cobble_to_gravel/${rock.type}`, 
            `tfc:rock/cobble/${rock.type}`,
            1, `tfc:rock/gravel/${rock.type}`
        )
    }


    // gravel, sand, sandstone, glass
    event.remove({ id: "oritech:pulverizer/sand_from_sandstone" });
    event.remove({ id: "oritech:grinder/sand_from_sandstone" });
    event.remove({ id: "oritech:grinder/sand_from_sandstone" });
    event.remove({ id: "immersiveengineering:crusher/gravel" });
    event.remove({ id: "immersiveengineering:crusher/glass" });
    event.remove({ id: "minecraft:glass" });
    // TODO make replacements using TFC different sand colors instead of minecraft gravel
    generalCrushing("glass_to_white_sand", "minecraft:glass", 1, "tfc:sand/white")
    for (const rock of rocks) {
        generalCrushing(`gravel_to_sand/${rock.type}`, `tfc:rock/gravel/${rock.type}`, 1, `tfc:sand/${rock.color}`)
    }
    for(const sandColor of sandColors) {
        // mirror the TFC IE Crossover recipes with oritech
        // except oritech machines don't seem to support chance outputs, only output counts
        pulverizer(`smooth_sandstone_to_sand/${sandColor}`, `tfc:smooth_sandstone/${sandColor}`, [{count: 1, item: `tfc:sand/${sandColor}`},{count: 1, item: "tfc:powder/saltpeter"}] )
        grinder(`smooth_sandstone_to_sand/${sandColor}`, `tfc:smooth_sandstone/${sandColor}`, [{count: 1, item: `tfc:sand/${sandColor}`},{count: 1, item: "tfc:powder/saltpeter"}] )
        
        pulverizer(`raw_sandstone_to_sand/${sandColor}`, `tfc:raw_sandstone/${sandColor}`, [{count: 1, item: `tfc:sand/${sandColor}`},{count: 1, item: "tfc:powder/saltpeter"}] )
        grinder(`raw_sandstone_to_sand/${sandColor}`, `tfc:raw_sandstone/${sandColor}`, [{count: 1, item: `tfc:sand/${sandColor}`},{count: 1, item: "tfc:powder/saltpeter"}] )
        
        pulverizer(`cut_sandstone_to_sand/${sandColor}`, `tfc:cut_sandstone/${sandColor}`, [{count: 1, item: `tfc:sand/${sandColor}`},{count: 1, item: "tfc:powder/saltpeter"}] )
        grinder(`cut_sandstone_to_sand/${sandColor}`, `tfc:cut_sandstone/${sandColor}`, [{count: 1, item: `tfc:sand/${sandColor}`},{count: 1, item: "tfc:powder/saltpeter"}] )
    }


    //// iron production
    // soy_solids production from firmalife soybean paste
    event.remove({
        id: 'firmalife:barrel_sealed/soybean_oil'
    })
    event.custom({
        type: 'tfc:barrel_sealed',
        duration: 24000,
        input_fluid: {
            amount: 100,
            fluid: 'minecraft:water'
        },
        input_item: {
            type: 'tfc:and',
            children: [
                { item: 'firmalife:food/soybean_paste' },
                { type: 'tfc:not_rotten' }
            ],
            count: 1
        },
        output_fluid: {
            amount: 250,
            id: 'firmalife:soybean_oil'
        },
        output_item: {
            id: 'kubejs:soy_solids',
            count: 1
        }
    }).id('kubejs:barrel_sealed/soybean_oil_and_soy_solids')

    // Legume ash washing and iron extraction
    event.custom({
        type: 'tfc:heating',
        ingredient: {
            type: 'tfc:and',
            children: [
                { tag: 'kubejs:legume_foods' },
                { type: 'tfc:not_rotten' }
            ]
        },
        result_item: {
            count: 1,
            id: 'kubejs:legume_ash'
        },
        temperature: 700
    }).id('kubejs:heating/legume_foods_to_ash')

    event.custom({
        type: 'tfc:heating',
        ingredient: {
            item: 'kubejs:soy_solids'
        },
        result_item: {
            count: 1,
            id: 'kubejs:legume_ash'
        },
        temperature: 700
    }).id('kubejs:heating/soy_solids')

    event.custom({
        type: 'tfc:barrel_sealed',
        duration: 7200,
        input_fluid: {
            amount: 200,
            fluid: 'minecraft:water'
        },
        input_item: {
            count: 1,
            item: 'kubejs:legume_ash'
        },
        output_item: {
            count: 1,
            id: 'kubejs:washed_legume_ash'
        }
    }).id('kubejs:barrel_sealed/wash_legume_ash')

    event.custom({
        type: 'tfc:heating',
        ingredient: {
            item: 'kubejs:washed_legume_ash'
        },
        result_fluid: {
            amount: 5,
            id: 'tfc:metal/cast_iron'
        },
        temperature: 1535
    }).id('kubejs:heating/washed_legume_ash')

})





