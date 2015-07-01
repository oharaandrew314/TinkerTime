package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.FileTransferTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class DownloadModAssetTask extends FileTransferTask {

	public static enum ModDownloadType { File, Image };

	private final Crawler<?> crawler;
	private final ModDownloadType type;

	public DownloadModAssetTask(Crawler<?> crawler, ModDownloadType type){
		super(null, null);
		this.crawler = crawler;
		this.type = type;
	}

	private URL getUrl() throws IOException{
		switch(type){
		case File: return crawler.getDownloadLink();
		case Image: return crawler.getImageUrl();
		default: throw new IllegalStateException();
		}
	}

	private Path getDest() throws IOException{
		Mod mod = crawler.getMod();
		switch(type){
		case File: return mod.getZipPath();
		case Image: return mod.getImagePath();
		default: throw new IllegalStateException();
		}
	}

	@Override
	public boolean execute() throws Exception {
		Path dest = getDest();
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
