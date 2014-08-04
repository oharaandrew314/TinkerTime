package aohara.tinkertime.workflows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.models.DownloadedFile;

public class CheckForUpdateWorkflow extends Workflow{
	
	private final Path newPagePath;
	
	public CheckForUpdateWorkflow(DownloadedFile existing){
		super("Checking for Update for " + existing.getNewestFileName()); // TODO: Try to get a name here
		try {
			newPagePath = Files.createTempFile("page", ".temp");
			newPagePath.toFile().deleteOnExit();
			queueDownload(existing.getPageUrl(), newPagePath);
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public Path getNewPagePath(){
		return newPagePath;
	}
}
