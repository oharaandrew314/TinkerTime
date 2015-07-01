package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

public abstract class SaveModTask extends WorkflowTask {

	private final ModUpdateCoordinatorImpl updateCoordinator;

	SaveModTask(ModUpdateCoordinatorImpl updateCoordinator) {
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

		public FromMod(ModUpdateCoordinatorImpl updateCoordinator, Mod mod){
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

		public FromCrawler(ModUpdateCoordinatorImpl updateCoordinator, Crawler<?> crawler) {
			super(updateCoordinator);
			this.crawler = crawler;
		}

		@Override
		protected Mod getMod() throws IOException {
			crawler.updatedMod();
			return crawler.getMod();
		}

	}

}
