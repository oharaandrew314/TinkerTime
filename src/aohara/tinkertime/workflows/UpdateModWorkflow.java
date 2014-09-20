package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.controllers.crawlers.CrawlerFactory;
import aohara.tinkertime.controllers.crawlers.ModCrawler;

/**
 * Workflow that will update the mod from the given page URL.
 * 
 * Will download and store the mod zip file, and save the updated mod information.
 * 
 * @author Andrew O'Hara
 */
public class UpdateModWorkflow extends CrawlerDownloadFileWorkflow {
	
	public UpdateModWorkflow(ModCrawler<?> crawler, Config config, ModStateManager sm) {
		super("Adding New Mod: " + crawler.url.getFile(),crawler, config.getModsPath());
		addTask(new MarkModUpdatedTask(this, sm, crawler));
	}
	
	public UpdateModWorkflow(URL url, Config config, ModStateManager sm){
		this(new CrawlerFactory().getModCrawler(url), config, sm);
	}
	
	private class MarkModUpdatedTask extends WorkflowTask {
		
		private final ModUpdateListener listener;
		private final ModCrawler<?> crawler;

		public MarkModUpdatedTask(Workflow workflow, ModUpdateListener listener, ModCrawler<?> crawler) {
			super(workflow);
			this.listener = listener;
			this.crawler = crawler;
		}

		@Override
		public Boolean call() throws Exception {
			listener.modUpdated(crawler.createMod(), false);
			return true;
		}

		@Override
		public int getTargetProgress() throws IOException {
			return 0;
		}

		@Override
		public String getTitle() {
			return "Registering Mod as Updated";
		}

	}
}
