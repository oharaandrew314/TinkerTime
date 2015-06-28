package aohara.tinkertime.workflows;

import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModMetaHelper;
import aohara.tinkertime.controllers.ModUpdateCoordinator;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.resources.ModMetaLoader;

import com.google.inject.Inject;

public class ModWorkflowBuilderFactory {
	
	private final TinkerConfig config;
	private final CrawlerFactory crawlerService;
	private final ModMetaHelper modMetaHelper;
	private final ModUpdateCoordinator updateCoordinator;
	private final ModMetaLoader modLoader;
	
	@Inject
	ModWorkflowBuilderFactory(TinkerConfig config, CrawlerFactory crawlerService, ModUpdateCoordinator updateCoordinator, ModMetaHelper modMetaHelper, ModMetaLoader modLoader) {
		this.config = config;
		this.crawlerService = crawlerService;
		this.updateCoordinator = updateCoordinator;
		this.modMetaHelper = modMetaHelper;
		this.modLoader = modLoader;
	}
	
	public ModWorkflowBuilder createBuilder(){
		return new ModWorkflowBuilder(config, crawlerService, updateCoordinator, modMetaHelper, modLoader);
	}

}
