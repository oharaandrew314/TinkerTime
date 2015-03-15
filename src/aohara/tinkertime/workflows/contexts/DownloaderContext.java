package aohara.tinkertime.workflows.contexts;

import java.io.IOException;
import java.nio.file.Path;

import aohara.tinkertime.crawlers.Crawler;

public abstract class DownloaderContext {
	
	public final Crawler<?> crawler;
	
	protected DownloaderContext(Crawler<?> crawler){
		this.crawler = crawler;
	}
	
	public abstract Path getCachedImagePath() throws IOException;
	public abstract Path getDownloadPath() throws IOException;
}
