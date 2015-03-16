package aohara.tinkertime.workflows.contexts;

import java.io.IOException;
import java.nio.file.Path;

import aohara.tinkertime.crawlers.Crawler;

public class DirectDownloaderContext extends DownloaderContext {
	
	private final Path imagePath, downloadPath;
	
	public DirectDownloaderContext(Crawler<?> crawler, Path imagePath, Path downloadPath){
		super(crawler);
		this.imagePath = imagePath;
		this.downloadPath = downloadPath;
	}

	@Override
	public Path getCachedImagePath() throws IOException {
		if (imagePath.toFile().isDirectory()){
			return imagePath.resolve(crawler.getImageUrl().getFile());
		}
		return imagePath;
	}

	@Override
	public Path getDownloadPath() throws IOException {
		if (downloadPath.toFile().isDirectory()){
			return downloadPath.resolve(crawler.getNewestFileName());
		}
		return downloadPath;
	}
}
