package aohara.tinkertime.workflows;

import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.workflows.tasks.CachePageTask;
import aohara.tinkertime.workflows.tasks.DownloadFileTask;

/**
 * Workflow that will Download the File that the given Crawler will discover.
 * 
 * @author Andrew O'Hara
 */
public class DownloadFileWorkflow extends Workflow{
	
	protected final Crawler<?, ?> crawler;

	public DownloadFileWorkflow(String name, Crawler<?, ?> crawler, Path destPath) {
		super(name);
		this.crawler = crawler;
		
		addTask(new CachePageTask(this, crawler));		
		addTask(new DownloadFileTask(this, crawler, destPath));
	}

}
