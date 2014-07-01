package aohara.tinkertime.views;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.models.Mod;

public class ModListCellRenderer implements ListCellRenderer<Mod> {
	
	private final ImageIcon checkIcon, xIcon, errorIcon;
	private final DefaultListCellRenderer def = new DefaultListCellRenderer();
	
	public ModListCellRenderer(){
		checkIcon = getImage("check.png");
		xIcon = getImage("x.png");
		errorIcon = getImage("exclamation.png");
	}
	
	private ImageIcon getImage(String name){
		return new ImageIcon(getClass().getClassLoader().getResource("icon/" + name));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Mod> list,
			Mod value, int index, boolean isSelected, boolean cellHasFocus) {
		
		JLabel label = (JLabel) def.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (ModManager.isDownloaded(value, new Config())){
			label.setIcon(value.isEnabled() ? checkIcon : xIcon);
		} else {
			label.setIcon(errorIcon);
		}
		
		
		return label;
	}


}
