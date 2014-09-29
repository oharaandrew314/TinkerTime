package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.crawlers.ModCrawler;
import aohara.tinkertime.models.Mod;

public class MarkModUpdatedTask extends WorkflowTask {
	
	private final ModUpdateListener listener;
	private final ModCrawler<?> crawler;
	private final Mod mod;

	public MarkModUpdatedTask(Workflow workflow, ModUpdateListener listener, ModCrawler<?> crawler) {
		super(workflow);
		this.listener = listener;
		this.crawler = crawler;
		this.mod = null;
	}
	
	public MarkModUpdatedTask(Workflow workflow, ModUpdateListener listener, Mod mod) {
		super(workflow);
		this.listener = listener;
		this.crawler = null;
		this.mod = mod;
	}

	@Override
	public Boolean call() throws Exception {
		if (mod != null){
			listener.modUpdated(mod, false);
		} else {
			listener.modUpdated(crawler.createMod(), false);
		}
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