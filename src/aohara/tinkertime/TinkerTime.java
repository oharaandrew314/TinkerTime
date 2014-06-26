package aohara.tinkertime;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.views.DirectoryChooser;

public class TinkerTime {
	
	public static final String NAME = "Tinker Time";
	
	public static void main(String[] args) {
		// Initialize Config
		Config config = new Config();
		if (config.getModsPath() == null || config.getKerbalPath() == null){
			new DirectoryChooser().setVisible(true);
		}

	}

}
