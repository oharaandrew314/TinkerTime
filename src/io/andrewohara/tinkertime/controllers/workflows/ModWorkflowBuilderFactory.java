package io.andrewohara.tinkertime.controllers.workflows;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ModLoader;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.models.mod.Mod;

import com.google.inject.Inject;

public class ModWorkflowBuilderFactory {

	private final CrawlerFactory crawlerService;
	private final ModUpdateCoordinatorImpl updateCoordinator;
	private final ModLoader modLoader;

	@Inject
	ModWorkflowBuilderFactory(CrawlerFactory crawlerService, ModUpdateCoordinatorImpl updateCoordinator, ModLoader modLoader) {
		this.crawlerService = crawlerService;
		this.updateCoordinator = updateCoordinator;
		this.modLoader = modLoader;
	}

	public ModWorkflowBuilder createBuilder(Mod mod){
		return new ModWorkflowBuilder(crawlerService, updateCoordinator, modLoader, mod);
	}

}
