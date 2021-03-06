package io.andrewohara.tinkertime.io.kspLauncher;

import io.andrewohara.tinkertime.models.ConfigData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class LinuxExecStrategy extends GameExecStrategy {

	@Override
	protected List<String> getCommands(ConfigData config) {
		Path path = config.getSelectedInstallation().getGameDataPath();
		path = path.resolve(use64Bit() ? "../KSP.x86_64" : "../KSP.x86");
		return Arrays.asList(path.toString());
	}

	private boolean use64Bit() {
		// run 64-bit if system is 64-bit
		try(BufferedReader r = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("uname -m").getInputStream()))){
			return r.readLine().toLowerCase().equals("x86_64");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
