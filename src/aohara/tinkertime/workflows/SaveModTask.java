package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.ModUpdateCoordinator;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.Mod;

abstract class SaveModTask extends WorkflowTask {
	
	private final ModUpdateCoordinator updateCoordinator;

	SaveModTask(ModUpdateCoordinator updateCoordinator) {
		super("Saving Mod");
		this.updateCoordinator = updateCoordinator;
	}

	@Override
	public boolean execute() throws IOException {
		updateCoordinator.modUpdated(this, getMod());
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}
	
	protected abstract Mod getMod() throws IOException;
	
	static class FromMod extends SaveModTask {
		
		private final Mod mod;
		
		FromMod(ModUpdateCoordinator updateCoordinator, Mod mod){
			super(updateCoordinator);
			this.mod = mod;
		}

		@Override
		protected Mod getMod() {
			return mod;
		}
	}
	
	static class FromCrawler extends SaveModTask {
		
		private final Crawler<?> crawler;

		FromCrawler(ModUpdateCoordinator updateCoordinator, Crawler<?> crawler) {
			super(updateCoordinator);
			this.crawler = crawler;
		}

		@Override
		protected Mod getMod() throws IOException {
			return crawler.getMod();
		}
		
	}
	
}
