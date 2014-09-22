package aohara.tinkertime.views;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import aohara.common.selectorPanel.ControlPanel;
import aohara.tinkertime.Config;
import aohara.tinkertime.models.Mod;

/**
 * Component which displays the Mod's image from a given URL.
 *
 * @author Andrew O'Hara
 */
public class ModImageView extends ControlPanel<Mod> {
	
	private final JLabel label = new JLabel();
	private final Config config;
	
	public ModImageView(Config config){
		this.config = config;
		panel.add(label);
	}
	
	@Override
	public void display(Mod element){
		if (element != null){
			try {
				super.display(element);
				Image cachedImage = ImageIO.read(config.getModImagePath(element).toFile());
				label.setIcon(cachedImage != null ? new ImageIcon(cachedImage) : null);
			} catch(IOException ex){
				// Do Nothing
			}
		}
	}
}
