package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.ModCrawler;
import aohara.tinkertime.models.Mod;

public class MarkModUpdatedTask extends WorkflowTask {
	
	private final ModUpdateListener listener;
	private final ModCrawler<?> crawler;
	private boolean deleted = false;

	public MarkModUpdatedTask(ModUpdateListener listener, ModCrawler<?> crawler) {
		this.listener = listener;
		this.crawler = crawler;
	}
	
	public static MarkModUpdatedTask notifyDeletion(ModUpdateListener listener, Mod mod){
		try {
			MarkModUpdatedTask task = new MarkModUpdatedTask(
				listener,
				new CrawlerFactory().getModCrawler(mod.getPageUrl())
			);
			task.deleted = true;
			return task;
		} catch (UnsupportedHostException e) {
			throw new IllegalStateException("This should not happen");
		}
	}

	@Override
	public Boolean call() throws Exception {
		listener.modUpdated(crawler.createMod(), deleted);
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