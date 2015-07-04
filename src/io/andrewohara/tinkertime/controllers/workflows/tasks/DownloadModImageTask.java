package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.content.ImageManager;
import io.andrewohara.common.workflows.tasks.FileTransferTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class DownloadModImageTask extends FileTransferTask {

	private final Crawler<?> crawler;
	private final Mod mod;
	private final ImageManager imageManager;

	private URLConnection connection;

	public DownloadModImageTask(Crawler<?> crawler, Mod mod) {
		super(null, null);
		this.crawler = crawler;
		this.mod = mod;
		imageManager = new ImageManager();
	}

	@Override
	public boolean execute() throws Exception {
		if (crawler.getImageUrl() != null){
			try (
					InputStream is = getConnection().getInputStream();
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					){
				transfer(is, os);
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
				img = imageManager.resizeImage(img, imageManager.scaleToFit(img, Mod.MAX_IMAGE_SIZE));
				mod.setImage(img);
			}
		}
		return true;
	}

	private synchronized URLConnection getConnection() throws IOException{
		if (connection == null){
			try {
				connection = crawler.getImageUrl().openConnection();
			} catch (NullPointerException e){
				throw new IOException(e);
			}
		}
		return connection;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return getConnection().getContentLength();
	}

}
