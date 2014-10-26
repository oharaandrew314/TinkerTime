package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.workflows.ModDownloaderContext;

public class MarkModUpdatedTask extends WorkflowTask {
	
	private final ModUpdateListener listener;
	private final ModDownloaderContext builder;
	private boolean deleted = false;

	public MarkModUpdatedTask(ModUpdateListener listener, ModDownloaderContext builder) {
		this.listener = listener;
		this.builder = builder;
	}
	
	public static MarkModUpdatedTask notifyDeletion(ModUpdateListener listener, Mod mod, TinkerConfig config){
		try {
			MarkModUpdatedTask task = new MarkModUpdatedTask(
				listener,
				ModDownloaderContext.create(mod.getPageUrl(), config)
			);
			task.deleted = true;
			return task;
		} catch (UnsupportedHostException e) {
			throw new IllegalStateException("This should not happen");
		}
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		listener.modUpdated(builder.createMod(), deleted);
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