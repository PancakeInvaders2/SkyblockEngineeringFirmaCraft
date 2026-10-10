package com.pancake.tfc.skyblock.design.reachability;

import com.pancake.tfc.skyblock.design.reachability.services.ReachabilityResult;
import com.pancake.tfc.skyblock.design.reachability.services.ReachabilityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.pancake.tfc.skyblock.design.persistence.entities")
@EnableJpaRepositories(
		basePackages = "com.pancake.tfc.skyblock.design.persistence.repositories"
)
public class ReachabilityTesterApplication
		implements CommandLineRunner {

	private static final Logger LOG =
			LogManager.getLogger(ReachabilityTesterApplication.class);

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

		String scenarioId = "vanilla_tfc";

		ReachabilityResult infinite = service.infiniteReachableResources(scenarioId);

		long durationNanos = System.nanoTime() - startNanos;

		LOG.debug(
				"Reachability calculation completed in {} ms",
				durationNanos / 1_000_000.0
		);
	}
}