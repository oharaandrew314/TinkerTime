package aohara.tinkertime.views;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import aohara.common.content.ImageManager;
import aohara.common.views.selectorPanel.SelectorView;
import aohara.tinkertime.models.ConfigFactory;
import aohara.tinkertime.models.Mod;

import com.google.inject.Inject;

/**
 * Component which displays the Mod's image from a given URL.
 *
 * @author Andrew O'Hara
 */
public class ModImageView extends SelectorView.AbstractSelectorView<Mod> {

	private static final Dimension MAX_IMAGE_SIZE = new Dimension(250, 250);
	private final ImageManager imageManager = new ImageManager();
	private final JLabel label = new JLabel();
	private final ConfigFactory configFactory;

	@Inject
	ModImageView(ConfigFactory configFactory){
		this.configFactory = configFactory;
		label.setMaximumSize(MAX_IMAGE_SIZE);
	}

	@Override
	public JComponent getComponent() {
		return label;
	}

	@Override
	protected void onElementChanged(Mod element) {
		try {
			if (element == null){
				throw new NoModSelectedException();
			}

			BufferedImage image = imageManager.getImage(element.getCachedImagePath(configFactory.getConfig()));
			Dimension size = imageManager.scaleToFit(image, new Dimension(MAX_IMAGE_SIZE.width, MAX_IMAGE_SIZE.height));
			image = imageManager.resizeImage(image, size);
			label.setIcon(new ImageIcon(image));

		} catch (IOException | NoModSelectedException e) {
			label.setIcon(null);
		}
	}

	@SuppressWarnings("serial")
	private static class NoModSelectedException extends Exception {}
}
