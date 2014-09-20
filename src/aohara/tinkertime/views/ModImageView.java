package aohara.tinkertime.views;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import aohara.common.selectorPanel.ControlPanel;
import aohara.tinkertime.content.ImageCache;
import aohara.tinkertime.models.Mod;

/**
 * Component which displays the Mod's image from a given URL.
 *
 * @author Andrew O'Hara
 */
public class ModImageView extends ControlPanel<Mod> {
	
	private final JLabel label = new JLabel();
	private final ImageCache cache;
	
	public ModImageView(ImageCache cache){
		this.cache = cache;
		panel.add(label);
	}
	
	@Override
	public void display(Mod element){
		if (element != null){
			super.display(element);
			Image cachedImage = cache.get(element);
			label.setIcon(cachedImage != null ? new ImageIcon(cachedImage) : null);
		}
	}
}
