package aohara.tinkertime.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import thirdParty.CompoundIcon;
import aohara.common.content.ImageManager;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.models.Mod;

/**
 * Custom ListCellRenderer for a Mod to be displayed on a JList.
 * 
 * Displays the Mod name as well as all status icons to the left of it.
 *  
 * @author Andrew O'Hara
 */
public class ModListCellRenderer implements ListCellRenderer<Mod> {
	
	private final ImageIcon checkIcon, xIcon, errorIcon, updateIcon, localIcon;
	private final DefaultListCellRenderer def = new DefaultListCellRenderer();
	private final ImageManager imageManager = new ImageManager("icon/");
	private final TinkerConfig config;
	
	public ModListCellRenderer(TinkerConfig config){
		this.config = config;
		checkIcon = loadIcon("glyphicons_152_check.png", new Color(70, 210, 70));
		xIcon = loadIcon("glyphicons_207_remove_2.png", new Color(205, 20, 20));
		errorIcon = loadIcon("glyphicons_078_warning_sign.png", new Color(215, 160, 0));
		updateIcon = loadIcon("glyphicons_213_up_arrow.png", new Color(255, 200, 0));
		localIcon = loadIcon("glyphicons_410_compressed.png", new Color(0, 0, 0));
	}
	
	private ImageIcon loadIcon(String name, Color colour){
		BufferedImage image = imageManager.getImage(name);
		image = colour != null ? imageManager.colorize(image, colour) : image;
		return new ImageIcon(image);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Mod> list,
			Mod mod, int index, boolean isSelected, boolean cellHasFocus) {

		// Compile list of icons
		LinkedList<ImageIcon> icons = new LinkedList<>();
		if (mod.isDownloaded(config)){
			icons.add(mod.isEnabled(config) ? checkIcon : xIcon);
		} else {
			icons.add(errorIcon);
		}
		
		if (mod.isUpdateAvailable()){
			icons.add(updateIcon);
		}
		
		if (mod.pageUrl == null){
			icons.add(localIcon);
		}
		
		// Create cell label
		String text = mod.name;
		if (mod.getSupportedVersion() != null){
			text = String.format("[%s] %s", mod.getSupportedVersion(), text);
		}
		JLabel label = (JLabel) def.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
		label.setIcon(new CompoundIcon(icons.toArray(new Icon[0])));
		return label;
	}


}
