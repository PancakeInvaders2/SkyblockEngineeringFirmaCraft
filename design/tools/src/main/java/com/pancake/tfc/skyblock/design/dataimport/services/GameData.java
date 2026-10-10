package com.pancake.tfc.skyblock.design.dataimport.services;

import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record GameData(
        Map<String, JsonNode> lootTables,
        Map<String, JsonNode> recipes,
        Map<String, Set<String>> itemTags,
        Map<String, Set<String>> fluidTags
) {}
