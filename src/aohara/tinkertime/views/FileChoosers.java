package aohara.tinkertime.views;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChoosers {
	
	public static Path chooseJsonFile(boolean save){
		return chooseJsonFile(Paths.get("mods.json"), save);
	}
	
	public static Path chooseJsonFile(Path defaultPath, boolean save){
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choose Mod Json File");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  // Only accept files
		chooser.setFileFilter(new FileNameExtensionFilter("Json File", "json"));  // Only accept JSON files
		chooser.setSelectedFile(defaultPath.toFile());
		
		return save ? showSaveDialog(chooser) : showOpenDialog(chooser);
	}
	
	public static Path chooseModZip(){
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Please select the mod zip to add.");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  // Only accept files
		chooser.setFileFilter(new FileNameExtensionFilter("Zip Archive", "zip"));

		return showOpenDialog(chooser);
	}
	
	//-- Helpers --------------------------------------------------------------
	
	private static Path showSaveDialog(JFileChooser chooser){
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
			return chooser.getSelectedFile().toPath();
		}
		return null;
	}
	
	private static Path showOpenDialog(JFileChooser chooser){
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			return chooser.getSelectedFile().toPath();
		}
		return null;
	}

}
