package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.controllers.crawlers.CrawlerFactory;
import aohara.tinkertime.models.Mod;

public class UpdateModWorkflow extends DownloadFileWorkflow {
	
	@SuppressWarnings("unchecked")
	public UpdateModWorkflow(URL url, Config config, ModStateManager sm) {
		super("Adding New Mod: " + url, new CrawlerFactory().getCrawler(url), config.getModsPath());
		addTask(new MarkModUpdatedTask(this, sm, (Crawler<Mod, ?>) crawler));
	}
	
	private class MarkModUpdatedTask extends WorkflowTask {
		
		private final ModUpdateListener listener;
		private final Crawler<Mod, ?> crawler;

		public MarkModUpdatedTask(Workflow workflow, ModUpdateListener listener, Crawler<Mod, ?> crawler) {
			super(workflow);
			this.listener = listener;
			this.crawler = crawler;
		}

		@Override
		public Boolean call() throws Exception {
			listener.modUpdated(crawler.crawl(), false);
			return true;
		}

		@Override
		public int getTargetProgress() throws IOException {
			return 0;
		}

	}
}
