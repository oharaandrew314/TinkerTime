package aohara.tinkertime.workflows.contexts;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;

public class DirectDownloaderContext extends DownloaderContext {
	
	private static final CrawlerFactory factory = new CrawlerFactory();
	private final Path imagePath, downloadPath;
	
	private DirectDownloaderContext(Crawler<?> crawler, Path imagePath, Path downloadPath){
		super(crawler);
		this.imagePath = imagePath;
		this.downloadPath = downloadPath;
	}
	
	public static DownloaderContext fromUrl(URL pageUrl, Path imagePath, Path downloadPath) throws UnsupportedHostException{
		return new DirectDownloaderContext(factory.getCrawler(pageUrl), imagePath, downloadPath);
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
