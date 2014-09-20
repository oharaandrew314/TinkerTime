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
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.models.UpdateableFile;
import aohara.tinkertime.workflows.tasks.CheckForUpdateTask;
import aohara.tinkertime.workflows.tasks.NotfiyUpdateAvailableTask;

/**
 * Workflow that checks if a File Update is available.
 * 
 * If an update is available, the given UpdateListener will be notified. 
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateWorkflow extends Workflow{
	
	private final Path newPagePath;
	
	public static CheckForUpdateWorkflow forExistingFile(
			UpdateableFile existing, boolean onlyUpdateIfNewer,
			FileUpdateListener... listeners){
		// Add mod to list of listeners
		ArrayList<FileUpdateListener> listenerList = new ArrayList<>();
		listenerList.add(existing);
		for (FileUpdateListener l : listeners){
			listenerList.add(l);
		}

		return new CheckForUpdateWorkflow(
			existing.getNewestFileName(),
			existing.getPageUrl(),
			existing.getUpdatedOn(),
			existing.getNewestFileName(),
			onlyUpdateIfNewer,
			listenerList.toArray(new FileUpdateListener[listenerList.size()])
		);
	}
	
	public CheckForUpdateWorkflow(
			String label, URL pageUrl, Date lastUpdated, String lastFileName,
			boolean onlyUpdateIfNewer, FileUpdateListener... listeners){
		super("Checking for Update for " +label);
		
		Crawler<?> crawler = new CrawlerFactory().getCrawler(pageUrl);
		
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
