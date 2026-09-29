package com.pancake.tfc.skyblock;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue STARTER_SPAWN_X = BUILDER
            .comment("X offset of the player spawn relative to the starter island origin.")
            .defineInRange("starterSpawnX", 4, -100, 100);

    public static final ModConfigSpec.IntValue STARTER_SPAWN_Y = BUILDER
            .comment("Y offset of the player spawn relative to the starter island origin.")
            .defineInRange("starterSpawnY", 1, -100, 100);

    public static final ModConfigSpec.IntValue STARTER_SPAWN_Z = BUILDER
            .comment("Z offset of the player spawn relative to the starter island origin.")
            .defineInRange("starterSpawnZ", 4, -100, 100);


    public static final ModConfigSpec.ConfigValue<List<? extends String>> STARTER_ISLAND_VARIANTS = BUILDER
            .comment(
                    "Starter island structures are selected according to the average annual temperature at the spawn location.",
                    "Each entry has the format: structure_id:max_average_temperature",
                    "Entries must be ordered from lowest to highest max temperature.",
                    "The last entry cannot have a max_average_temperature",
                    "ex: [\"starter_island_cold:5\", \"starter_island_temperate:18\",\"starter_island_hot\"]",
                    "to use starter_island_cold.nbt for spawns with < 5°C average temperature, starter_island_temperate.nbt for spawns < 18°C average temperature, and starter_island_hot.nbt otherwise"
            )
            .defineListAllowEmpty(
                    "starterIslandVariants",
                    List.of(
                            "starter_island_cold:5",
                            "starter_island_temperate:18",
                            "starter_island_hot"
                    ),
                    () -> "",
                    Config::validateStarterIslandVariant
            );

    static final ModConfigSpec SPEC = BUILDER.build();


    private static boolean validateStarterIslandVariant(Object value) {
        if (!(value instanceof String entry)) {
            return false;
        }

        String[] parts = entry.split(":", 2);

        if (parts[0].isBlank()) {
            return false;
        }

        if (parts.length == 1) {
            return true;
        }

        try {
            Float.parseFloat(parts[1]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}