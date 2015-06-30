package io.andrewohara.tinkertime.controllers.workflows;

import io.andrewohara.tinkertime.controllers.ModMetaHelper;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.models.ConfigFactory;

import com.google.inject.Inject;

public class ModWorkflowBuilderFactory {

	private final ConfigFactory configFactory;
	private final CrawlerFactory crawlerService;
	private final ModMetaHelper modMetaHelper;
	private final ModUpdateCoordinator updateCoordinator;

	@Inject
	ModWorkflowBuilderFactory(
			ConfigFactory configFactory, CrawlerFactory crawlerService,
			ModUpdateCoordinator updateCoordinator, ModMetaHelper modMetaHelper
			) {
		this.configFactory = configFactory;
		this.crawlerService = crawlerService;
		this.updateCoordinator = updateCoordinator;
		this.modMetaHelper = modMetaHelper;
	}

	public ModWorkflowBuilder createBuilder(){
		return new ModWorkflowBuilder(configFactory, crawlerService, updateCoordinator, modMetaHelper);
	}

}
