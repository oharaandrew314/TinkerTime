package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.models.DownloadedFile;
import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.workflows.tasks.CheckForUpdateTask;
import aohara.tinkertime.workflows.tasks.NotfiyUpdateAvailableTask;

public class CheckForUpdateWorkflow extends Workflow{
	
	private final Path newPagePath;
	
	public static CheckForUpdateWorkflow forExistingFile(DownloadedFile existing, UpdateListener... listeners){
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
			listenerList.toArray(new UpdateListener[listenerList.size()])
		);
	}
	
	public CheckForUpdateWorkflow(
			String label, URL pageUrl, Date lastUpdated, String lastFileName,
			UpdateListener... listeners){
		super("Checking for Update for " +label);
		try {
			newPagePath = Files.createTempFile("page", ".temp");
			newPagePath.toFile().deleteOnExit();
			queueDownload(pageUrl, newPagePath);
			addTask(new CheckForUpdateTask(this, newPagePath, pageUrl, lastUpdated, lastFileName));
			addTask(new NotfiyUpdateAvailableTask(this, newPagePath, pageUrl, listeners));
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
}
