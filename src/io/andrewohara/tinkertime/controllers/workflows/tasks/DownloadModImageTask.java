package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.content.ImageManager;
import io.andrewohara.common.workflows.tasks.FileTransferTask;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.ModImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class DownloadModImageTask extends FileTransferTask {

	private final Crawler<?> crawler;
	private final ModUpdateCoordinator updateCoordinator;
	private final ImageManager imageManager;

	private URLConnection connection;

	public DownloadModImageTask(Crawler<?> crawler, ModUpdateCoordinator updateCoordinator) {
		super(null, null);
		this.crawler = crawler;
		this.updateCoordinator = updateCoordinator;
		imageManager = new ImageManager();
	}

	@Override
	public boolean execute() throws Exception {
		try (
				InputStream is = getConnection().getInputStream();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				){
			transfer(is, os);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			img = imageManager.resizeImage(img, imageManager.scaleToFit(img, ModImage.MAX_SIZE));
			updateCoordinator.updateModImage(crawler.getMod(), img);
		}

		return true;
	}

	private synchronized URLConnection getConnection() throws IOException{
		if (connection == null){
			connection = crawler.getImageUrl().openConnection();
		}
		return connection;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return getConnection().getContentLength();
	}

}
