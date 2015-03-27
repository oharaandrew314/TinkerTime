package aohara.tinkertime.workflows;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.workflows.ModWorkflowBuilder.ModDownloadType;
import aohara.tinkertime.workflows.contexts.DownloaderContext;

class MoveCrawlerDownloadToDestTask extends WorkflowTask {
	
	private final DownloaderContext context;
	private final ModDownloadType type;
	private final Path src;
	
	MoveCrawlerDownloadToDestTask(DownloaderContext context, ModDownloadType type, Path src) {
		super("Moving from temp to dest");
		this.context = context;
		this.type = type;
		this.src = src;
	}

	@Override
	public boolean execute() throws Exception {
		Path dest = null;
		switch(type){
		case File: dest = context.getDownloadPath(); break;
		case Image: dest = context.getCachedImagePath(); break;
		default:
		}
		
		return new FileTransferTask(src.toUri(), dest).call(getWorkflow());
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return (int) src.toFile().length();
	}
}
