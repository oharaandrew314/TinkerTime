package io.andrewohara.tinkertime.views;

import java.awt.Component;

import javax.swing.JOptionPane;

public class TinkerDialogs {
	
	public static boolean confirmDeleteMod(Component parent, String modName){
		return (JOptionPane.showConfirmDialog(
			parent,
			"Are you sure you want to delete "
			+ modName + "?",
			"Delete?",
			JOptionPane.YES_NO_OPTION
		) == JOptionPane.YES_OPTION);
	}

}
