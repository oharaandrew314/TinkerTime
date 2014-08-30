package aohara.tinkertime.workflows;

import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.workflows.tasks.CachePageTask;
import aohara.tinkertime.workflows.tasks.DownloadFileTask;

public class DownloadFileWorkflow extends Workflow{

	public DownloadFileWorkflow(String name, Crawler<?, ?> crawler, Path destPath) {
		super(name);
		
		addTask(new CachePageTask(this, crawler));		
		addTask(new DownloadFileTask(this, crawler, destPath));
	}

}
