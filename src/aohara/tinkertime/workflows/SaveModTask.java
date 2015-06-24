package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModMetaLoader;

abstract class SaveModTask extends WorkflowTask {
	
	private final ModMetaLoader modLoader;

	SaveModTask(ModMetaLoader modLoader) {
		super("Saving Mod");
		this.modLoader = modLoader;
	}

	@Override
	public boolean execute() throws IOException {
		modLoader.modUpdated(getMod());
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}
	
	protected abstract Mod getMod() throws IOException;
	
	static class FromMod extends SaveModTask {
		
		private final Mod mod;
		
		FromMod(ModMetaLoader modLoader, Mod mod){
			super(modLoader);
			this.mod = mod;
		}

		@Override
		protected Mod getMod() {
			return mod;
		}
	}
	
	static class FromCrawler extends SaveModTask {
		
		private final Crawler<?> crawler;

		FromCrawler(ModMetaLoader modLoader, Crawler<?> crawler) {
			super(modLoader);
			this.crawler = crawler;
		}

		@Override
		protected Mod getMod() throws IOException {
			return crawler.getMod();
		}
		
	}
	
}
