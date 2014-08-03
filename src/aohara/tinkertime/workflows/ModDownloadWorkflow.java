package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.Config;

public class ModDownloadWorkflow extends Workflow {

	public ModDownloadWorkflow(Config config, URL url) throws IOException {
		super("Downloading " + url);
		queueTempDownload(url, config.getModsPath());
	}

}
