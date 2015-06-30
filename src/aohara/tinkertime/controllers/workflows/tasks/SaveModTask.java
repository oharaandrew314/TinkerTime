package aohara.tinkertime.controllers.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import aohara.tinkertime.io.crawlers.Crawler;
import aohara.tinkertime.models.Mod;

public abstract class SaveModTask extends WorkflowTask {

	private final ModUpdateCoordinator updateCoordinator;

	SaveModTask(ModUpdateCoordinator updateCoordinator) {
		super("Saving Mod");
		this.updateCoordinator = updateCoordinator;
	}

	@Override
	public boolean execute() throws IOException {
		updateCoordinator.updateMod(getMod());
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

	protected abstract Mod getMod() throws IOException;

	public static class FromMod extends SaveModTask {

		private final Mod mod;

		public FromMod(ModUpdateCoordinator updateCoordinator, Mod mod){
			super(updateCoordinator);
			this.mod = mod;
		}

		@Override
		protected Mod getMod() {
			return mod;
		}
	}

	public static class FromCrawler extends SaveModTask {

		private final Crawler<?> crawler;

		public FromCrawler(ModUpdateCoordinator updateCoordinator, Crawler<?> crawler) {
			super(updateCoordinator);
			this.crawler = crawler;
		}

		@Override
		protected Mod getMod() throws IOException {
			return crawler.getMod();
		}

	}

}
