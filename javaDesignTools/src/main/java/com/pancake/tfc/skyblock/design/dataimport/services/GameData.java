package com.pancake.tfc.skyblock.design.dataimport.services;

import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public record GameData(
        Map<String, JsonNode> lootTables,
        Map<String, JsonNode> recipes,
        Map<String, List<String>> itemTags,
        Map<String, List<String>> fluidTags
) {}
