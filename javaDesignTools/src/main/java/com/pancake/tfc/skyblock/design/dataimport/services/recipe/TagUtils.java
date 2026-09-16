package com.pancake.tfc.skyblock.design.dataimport.services.recipe;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;

import java.util.List;
import java.util.Set;

public class TagUtils {

    private static final List<String> FLUID_TAG_NAMESPACES = List.of("tfc", "c");


    public static Set<String> getFluidTagResourceIds(String fluidTagId, GameData gameData) {
        Set<String> tagResources = gameData.fluidTags().get(fluidTagId);

        // preferential match for the tag as given in the recipe
        if (tagResources != null && !tagResources.isEmpty()) {
            return tagResources;
        }

        // some referenced tags like "tfc:alcohols" exist in the other namespace as "c:alcohols" instead
        // if the tag isn't found, check the other namespaces before giving up

        int colonIndex = fluidTagId.indexOf(":");
        String name = fluidTagId.substring(colonIndex+1);

        for (String fluidTagNamespace : FLUID_TAG_NAMESPACES) {
            tagResources = gameData.fluidTags().get(fluidTagNamespace + ":" + name);

            if (tagResources != null && !tagResources.isEmpty()) {
                return tagResources;
            }
        }

        throw new IllegalArgumentException(
                "Unknown or empty fluid tag: " + fluidTagId
        );

    }

    public static Set<String> getItemTagResourceIds(
            String itemTagId,
            GameData gameData
    ) {
        Set<String> tagResources = gameData.itemTags().get(itemTagId);

        // Prefer the tag as given in the recipe.
        if (tagResources != null && !tagResources.isEmpty()) {
            return tagResources;
        }

        // Some referenced tags can exist under another namespace
        // in the effective runtime tag data.
        int colonIndex = itemTagId.indexOf(":");

        if (colonIndex < 0) {
            throw new IllegalArgumentException(
                    "Invalid item tag: " + itemTagId
            );
        }

        String name = itemTagId.substring(colonIndex + 1);

        for (String namespace : List.of("tfc", "c")) {
            if (namespace.equals(itemTagId.substring(0, colonIndex))) {
                continue;
            }

            tagResources = gameData.itemTags().get(namespace + ":" + name);

            if (tagResources != null && !tagResources.isEmpty()) {
                return tagResources;
            }
        }

        throw new IllegalArgumentException(
                "Unknown or empty item tag: " + itemTagId
        );
    }
}
