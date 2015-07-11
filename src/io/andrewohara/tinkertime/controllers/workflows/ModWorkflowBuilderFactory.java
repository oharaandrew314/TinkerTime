package io.andrewohara.tinkertime.controllers.workflows;

import io.andrewohara.tinkertime.controllers.ModLoader;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class ModWorkflowBuilderFactory {

	private final CrawlerFactory crawlerService;
	private final ModLoader modLoader;
	private final Dao<ModFile, Integer> modFilesDao;

	@Inject
	ModWorkflowBuilderFactory(CrawlerFactory crawlerService, ModLoader modLoader, Dao<ModFile, Integer> modFilesDao) {
		this.crawlerService = crawlerService;
		this.modLoader = modLoader;
		this.modFilesDao = modFilesDao;
	}

	public ModWorkflowBuilder createBuilder(Mod mod){
		return new ModWorkflowBuilder(crawlerService, modLoader, modFilesDao, mod);
	}

}
