package com.pancake.tfc.skyblock.design.dataimport;

import com.pancake.tfc.skyblock.design.dataimport.services.GameData;
import com.pancake.tfc.skyblock.design.dataimport.services.GameDataLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@EntityScan(basePackages = "com.pancake.tfc.skyblock.design.persistence.entities")
@EnableJpaRepositories(
        basePackages = "com.pancake.tfc.skyblock.design.persistence.repositories"
)
public class DataImportApplication
        implements CommandLineRunner {

    private static final Logger LOG =
            LogManager.getLogger(DataImportApplication.class);

    private final GameDataLoader service;

    public DataImportApplication(
            GameDataLoader service) {
        this.service = service;
    }

    public static void main(String[] args) {

        SpringApplication.run(
                DataImportApplication.class,
                args
        );

    }

    @Override
    public void run(String... args) throws IOException, URISyntaxException {
        long startNanos = System.nanoTime();

        GameData gameData = service.load();

        LOG.info("gameData : {} tags", gameData.itemTags().size());
        LOG.info("gameData : {} loot tables", gameData.lootTables().size());
        LOG.info("gameData : {} recipes", gameData.recipes().size());

        LOG.info("recipe anvil/wrought_iron: {}", gameData.recipes().get("tfc:anvil/wrought_iron"));
        LOG.info("crocodile : {}", gameData.lootTables().get("tfc:entities/crocodile"));
        LOG.info("tag : {}", gameData.itemTags().get("tfc:anvils"));

        long durationNanos = System.nanoTime() - startNanos;

        LOG.debug(
                "Data parsed in {} ms",
                durationNanos / 1_000_000.0
        );
    }
}