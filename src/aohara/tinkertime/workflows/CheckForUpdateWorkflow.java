package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.controllers.crawlers.CrawlerFactory;
import aohara.tinkertime.models.DownloadedFile;
import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.workflows.tasks.CheckForUpdateTask;
import aohara.tinkertime.workflows.tasks.NotfiyUpdateAvailableTask;

public class CheckForUpdateWorkflow extends Workflow{
	
	private final Path newPagePath;
	
	public static CheckForUpdateWorkflow forExistingFile(
			DownloadedFile existing, boolean onlyUpdateIfNewer,
			UpdateListener... listeners){
		// Add mod to list of listeners
		ArrayList<UpdateListener> listenerList = new ArrayList<>();
		listenerList.add(existing);
		for (UpdateListener l : listeners){
			listenerList.add(l);
		}

		return new CheckForUpdateWorkflow(
			existing.getNewestFileName(),
			existing.getPageUrl(),
			existing.getUpdatedOn(),
			existing.getNewestFileName(),
			onlyUpdateIfNewer,
			listenerList.toArray(new UpdateListener[listenerList.size()])
		);
	}
	
	public CheckForUpdateWorkflow(
			String label, URL pageUrl, Date lastUpdated, String lastFileName,
			boolean onlyUpdateIfNewer, UpdateListener... listeners){
		super("Checking for Update for " +label);
		
		Crawler<?, ?> crawler = new CrawlerFactory().getCrawler(pageUrl);
		
		try {
			newPagePath = Files.createTempFile("page", ".temp");
			newPagePath.toFile().deleteOnExit();
			queueDownload(pageUrl, newPagePath);
			if (onlyUpdateIfNewer){
				addTask(new CheckForUpdateTask(this, crawler, lastUpdated, lastFileName));
			}
			addTask(new NotfiyUpdateAvailableTask(this, crawler, listeners));
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
}
