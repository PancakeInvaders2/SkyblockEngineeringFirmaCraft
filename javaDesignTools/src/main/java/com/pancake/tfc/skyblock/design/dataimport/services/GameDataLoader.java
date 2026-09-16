package com.pancake.tfc.skyblock.design.dataimport.services;

import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class GameDataLoader {

    private static final String JSON_EXTENSION = ".json";

    private final ObjectMapper objectMapper;

    public GameDataLoader() {
        this.objectMapper = new ObjectMapper();
    }

    public GameData load() throws IOException, URISyntaxException {

        URL resource = GameDataLoader.class.getClassLoader().getResource("extracted_json_data");
        assert resource != null;
        Path jsonDataDirectory = Paths.get(resource.toURI());

        Path lootTablesDirectory = jsonDataDirectory.resolve("loot_tables");
        Path recipesDirectory = jsonDataDirectory.resolve("recipes");
        Path tagsDirectory = jsonDataDirectory.resolve("tags/item");
        Path fluidTagsDirectory = jsonDataDirectory.resolve("tags/fluid");

        Map<String, JsonNode> lootTables = loadJsonObjects(
                lootTablesDirectory
        );

        Map<String, JsonNode> recipes = loadJsonObjects(
                recipesDirectory
        );

        Map<String, Set<String>> itemTags = loadTags(
                tagsDirectory
        );

        Map<String, Set<String>> fluidTags = loadTags(
                fluidTagsDirectory
        );

        return new GameData(
                lootTables,
                recipes,
                itemTags,
                fluidTags
        );
    }

    private Map<String, JsonNode> loadJsonObjects(Path root) throws IOException {
        Map<String, JsonNode> result = new HashMap<>();

        if (!Files.exists(root)) {
            return result;
        }

        try (Stream<Path> files = Files.walk(root)) {
            files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(JSON_EXTENSION))
                    .forEach(path -> {
                        String id = minecraftId(root, path);
                        JsonNode json = objectMapper.readTree(path.toFile());

                        result.put(id, json);
                    });
        } catch (DataLoadException e) {
            throw e;
        }

        return result;
    }

    private Map<String, Set<String>> loadTags(Path root) throws IOException {
        Map<String, Set<String>> result = new HashMap<>();

        if (!Files.exists(root)) {
            return result;
        }

        try (Stream<Path> files = Files.walk(root)) {
            files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(JSON_EXTENSION))
                    .forEach(path -> {
                        String tagId = minecraftId(root, path);

                        JsonNode json = objectMapper.readTree(path.toFile());

                        if (!json.isArray()) {
                            throw new DataLoadException(
                                    "Expected tag to contain an array: " + path
                            );
                        }

                        Set<String> items = new HashSet<>();

                        json.forEach(item -> {
                            if (!item.isString()) {
                                throw new DataLoadException(
                                        "Expected tag entry to be a string: "
                                                + path
                                                + " -> "
                                                + item
                                );
                            }

                            items.add(item.stringValue());
                        });

                        result.put(tagId, items);
                    });
        } catch (DataLoadException e) {
            throw e;
        }

        return result;
    }

    private String minecraftId(Path root, Path file) {
        Path relative = root.relativize(file);

        List<String> parts = new ArrayList<>();

        for (Path part : relative) {
            parts.add(part.toString());
        }

        if (parts.isEmpty()) {
            throw new DataLoadException(
                    "Could not determine Minecraft ID from " + file
            );
        }

        String filename = parts.remove(parts.size() - 1);

        if (!filename.endsWith(JSON_EXTENSION)) {
            throw new DataLoadException(
                    "Not a JSON file: " + file
            );
        }

        filename = filename.substring(
                0,
                filename.length() - JSON_EXTENSION.length()
        );

        if (parts.isEmpty()) {
            throw new DataLoadException(
                    "Missing namespace in " + file
            );
        }

        String namespace = parts.remove(0);

        StringBuilder id = new StringBuilder(namespace);

        id.append(':');

        for (String part : parts) {
            id.append(part).append('/');
        }

        id.append(filename);

        return id.toString();
    }

    public static class DataLoadException extends RuntimeException {

        public DataLoadException(String message) {
            super(message);
        }

        public DataLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}