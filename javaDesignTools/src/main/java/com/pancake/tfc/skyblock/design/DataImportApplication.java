package com.pancake.tfc.skyblock.design;

import com.pancake.tfc.skyblock.design.services.dataimport.GameData;
import com.pancake.tfc.skyblock.design.services.dataimport.GameDataLoader;
import com.pancake.tfc.skyblock.design.services.reachability.ReachabilityResult;
import com.pancake.tfc.skyblock.design.services.reachability.ReachabilityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
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

        long durationNanos = System.nanoTime() - startNanos;

        LOG.debug(
                "Reachability calculation completed in {} ms",
                durationNanos / 1_000_000.0
        );
    }
}