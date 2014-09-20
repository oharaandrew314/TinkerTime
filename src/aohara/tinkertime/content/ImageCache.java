package aohara.tinkertime.content;

import java.awt.Image;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import aohara.common.workflows.Workflows;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.models.Mod;

public class ImageCache implements ModUpdateListener {
	
	private final Executor downloader = Executors.newFixedThreadPool(3);
	private final Path cacheFolder;
	
	public ImageCache(Config config){
		this(config.getFolder().resolve("cache"));
	}
	
	private ImageCache(Path cacheFolder){
		this.cacheFolder = cacheFolder;
	}
	
	public Image get(Mod mod){
		try {
			return ImageIO.read(getImagePath(mod).toFile());
		} catch (IOException e) {
			return null;
		}
	}
	
	private Path getImagePath(Mod mod){
		Path imageName = Paths.get(mod.getPageUrl().getFile()).getFileName();
		return cacheFolder.resolve(imageName);
	}

	@Override
	public void modUpdated(Mod mod, boolean deleted) {
		Path imagePath = getImagePath(mod);
		imagePath.toFile().delete();
		
		if (!deleted){
			try {
				downloader.execute(Workflows.tempDownload(mod.getImageUrl(), imagePath));
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
