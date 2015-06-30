package aohara.tinkertime.controllers.workflows;

import aohara.tinkertime.controllers.ModMetaHelper;
import aohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import aohara.tinkertime.io.crawlers.CrawlerFactory;
import aohara.tinkertime.models.ConfigFactory;

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
