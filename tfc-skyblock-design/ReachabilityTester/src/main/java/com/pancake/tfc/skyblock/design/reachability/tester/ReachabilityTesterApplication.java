package com.pancake.tfc.skyblock.design.reachability.tester;

import com.pancake.tfc.skyblock.design.reachability.tester.entities.Scenario;
import com.pancake.tfc.skyblock.design.reachability.tester.entities.ScenarioResource;
import com.pancake.tfc.skyblock.design.reachability.tester.repositories.ScenarioRepository;
import com.pancake.tfc.skyblock.design.reachability.tester.service.ReachabilityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ReachabilityTesterApplication
		implements CommandLineRunner {

	private static final Logger LOG =
			LogManager.getLogger(ReachabilityService.class);

	private final ReachabilityService service;

	public ReachabilityTesterApplication(
			ReachabilityService service) {
		this.service = service;
	}

	public static void main(String[] args) {

		SpringApplication.run(
				ReachabilityTesterApplication.class,
				args
		);

	}

	@Override
	public void run(String... args) {
		long startNanos = System.nanoTime();

		service.reachableResources("vanilla_tfc");

		long durationNanos = System.nanoTime() - startNanos;

		LOG.debug(
				"Reachability calculation completed in {} ms",
				durationNanos / 1_000_000.0
		);
	}
}