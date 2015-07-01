package io.andrewohara.tinkertime.controllers.workflows;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.models.mod.Mod;

import com.google.inject.Inject;

public class ModWorkflowBuilderFactory {

	private final CrawlerFactory crawlerService;
	private final ModUpdateCoordinator updateCoordinator;

	@Inject
	ModWorkflowBuilderFactory(CrawlerFactory crawlerService, ModUpdateCoordinator updateCoordinator) {
		this.crawlerService = crawlerService;
		this.updateCoordinator = updateCoordinator;
	}

	public ModWorkflowBuilder createBuilder(Mod mod){
		return new ModWorkflowBuilder(crawlerService, updateCoordinator, mod);
	}

}
