package aohara.tinkertime.workflows;

import java.net.URL;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.controllers.crawlers.CrawlerFactory;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.workflows.tasks.CachePageTask;
import aohara.tinkertime.workflows.tasks.DownloadModTask;

public class UpdateModWorkflow extends Workflow {

	public UpdateModWorkflow(URL url, Config config, ModStateManager sm) {
		super("Adding New Mod: " + url);
		
		@SuppressWarnings("unchecked") // FIXME
		Crawler<Mod, ?> crawler = (Crawler<Mod, ?>) new CrawlerFactory().getCrawler(url);
		
		// Add Tasks
		addTask(new CachePageTask(this, crawler));
		addTask(new DownloadModTask(this, crawler, config, sm));
	}
}
