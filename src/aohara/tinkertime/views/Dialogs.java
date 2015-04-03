package aohara.tinkertime.views;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialogs {
	
	public static void errorDialog(Component parent, Throwable throwable){
		JOptionPane.showMessageDialog(parent, throwable.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
	}
	
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
