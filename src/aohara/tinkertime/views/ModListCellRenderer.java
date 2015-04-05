package aohara.tinkertime.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import thirdParty.CompoundIcon;
import aohara.common.Util;
import aohara.common.content.ImageManager;
import aohara.tinkertime.ModManager.ModNotDownloadedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;

/**
 * Custom ListCellRenderer for a Mod to be displayed on a JList.
 * 
 * Displays the Mod name as well as all status icons to the left of it.
 *  
 * @author Andrew O'Hara
 */
public class ModListCellRenderer implements ListCellRenderer<Mod> {
	
	private static final ImageManager imageManager = new ImageManager("icon/");
	private static final ImageIcon
		CHECK_ICON = loadIcon("glyphicons_152_check.png", "Mod Enabled", new Color(70, 210, 70)),
		X_ICON = loadIcon("glyphicons_207_remove_2.png", "Mod Disabled", new Color(205, 20, 20)),
		ERROR_ICON = loadIcon("glyphicons_078_warning_sign.png", "Mod Zip not found.  Please update", new Color(215, 160, 0)),
		UPDATE_ICON = loadIcon("glyphicons_213_up_arrow.png", "Update Available", new Color(255, 200, 0)),
		LOCAL_ICON = loadIcon("glyphicons_410_compressed.png", "Mod added locally.  Not updateable", new Color(0, 0, 0));
	
	private static final DefaultListCellRenderer DEFAULT_RENDERER = new DefaultListCellRenderer();
	private final ModLoader modLoader;
	
	public ModListCellRenderer(ModLoader modLoader){
		this.modLoader = modLoader;
	}
	
	private static ImageIcon loadIcon(String name, String description, Color colour){
		BufferedImage image = imageManager.getImage(name);
		image = colour != null ? imageManager.colorize(image, colour) : image;
		return new ImageIcon(image, description);
	}
	
	private ImageIcon[] getCurrentIcons(Mod mod){
		LinkedList<ImageIcon> icons = new LinkedList<>();
		try {
			icons.add(modLoader.isEnabled(mod) ? CHECK_ICON : X_ICON);
		} catch (ModNotDownloadedException e){
			icons.add(ERROR_ICON);
		}
		
		if (mod.updateAvailable){
			icons.add(UPDATE_ICON);
		}
		
		if (mod.pageUrl == null){
			icons.add(LOCAL_ICON);
		}
		return icons.toArray(new ImageIcon[0]);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Mod> list,
			Mod mod, int index, boolean isSelected, boolean cellHasFocus) {

		// Compile list of icons
		ImageIcon[] icons = getCurrentIcons(mod);
		
		// Create cell label
		String text = mod.name;
		if (mod.getSupportedVersion() != null){
			text = String.format("[%s] %s", mod.getSupportedVersion(), text);
		}
		
		String tooltipText = String.format("<html>%s</html>", Util.joinStrings(icons, "<br/>"));
		
		JLabel label = (JLabel) DEFAULT_RENDERER.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
		label.setToolTipText(tooltipText);
		label.setIcon(new CompoundIcon(icons));
		return label;
	}


}
