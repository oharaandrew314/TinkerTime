package aohara.tinkertime.workflows.tasks;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.common.workflows.tasks.FileTransferTask;
import aohara.tinkertime.workflows.ModWorkflowBuilder.ModDownloadType;
import aohara.tinkertime.workflows.contexts.DownloaderContext;

public class MoveCrawlerDownloadToDestTask extends WorkflowTask {
	
	private final DownloaderContext context;
	private final ModDownloadType type;
	private final Path src;
	
	public MoveCrawlerDownloadToDestTask(DownloaderContext context, ModDownloadType type, Path src) {
		this.context = context;
		this.type = type;
		this.src = src;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return (int) src.toFile().length();
	}

	@Override
	public String getTitle() {
		return "Moving from temp to dest";
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		Path dest = null;
		switch(type){
		case File: dest = context.getDownloadPath(); break;
		case Image: dest = context.getCachedImagePath(); break;
		default:
		}
		
		return new FileTransferTask(src.toUri(), dest).call(workflow);
	}
}
