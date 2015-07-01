package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.FileTransferTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class DownloadModZipTask extends FileTransferTask {

	private final Crawler<?> crawler;

	public DownloadModZipTask(Crawler<?> crawler){
		super(null, null);
		this.crawler = crawler;
	}

	private URL getUrl() throws IOException{
		return crawler.getDownloadLink();
	}

	@Override
	public boolean execute() throws Exception {
		Path dest = crawler.getMod().getZipPath();
		Path tempDest = Paths.get(dest.toString() + ".tempDownload");

		try {
			transfer(getUrl(), tempDest);  // Copy to temp file
			Files.move(tempDest, dest, StandardCopyOption.REPLACE_EXISTING);  // Rename to dest file
		} catch (NullSourceException e){
			// Do Nothing
		}

		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		URL url = getUrl();
		if (url != null){
			return url.openConnection().getContentLength();
		}
		return -1;
	}
}
