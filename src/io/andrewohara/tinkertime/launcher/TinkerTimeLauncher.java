package io.andrewohara.tinkertime.launcher;

import io.andrewohara.common.version.Version;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Main Class for Tinker Time
 *
 * @author Andrew O'Hara
 */
public class TinkerTimeLauncher implements Runnable {

	public static final String
	NAME = "Tinker Time",
	AUTHOR = "oharaandrew314",
	DOWNLOAD_URL = "https://kerbalstuff.com/mod/243";
	public static final Version VERSION = Version.valueOf("1.4.5");
	public static final String
	SAFE_NAME = NAME.replace(" ", ""),
	FULL_NAME = String.format("%s v%s by %s", NAME, VERSION, AUTHOR);

	public static String getDbUrl(){
		Path path = Paths.get(System.getProperty("user.home"), "Documents", NAME, "TinkerTime-db");
		//return String.format("jdbc:sqlite:file:%s", path.toString());
		return String.format("jdbc:h2:file:%s", path.toString());
	}

	private final Collection<Runnable> startupTasks = new LinkedList<>();

	@Inject
	TinkerTimeLauncher(DatabaseMigrator migrator, ConfigVerifier configVerifier, SetupListeners setupListeners, LoadModsTask startupModLoader, UpdateChecker updateChecker, MainFrameLauncher mainFrameLauncher){
		startupTasks.add(migrator);
		startupTasks.add(configVerifier);
		startupTasks.add(setupListeners);
		startupTasks.add(startupModLoader);
		startupTasks.add(updateChecker);
		startupTasks.add(mainFrameLauncher);
	}

	@Override
	public void run() {
		System.setProperty("http.agent", "TinkerTime Mod Manager Agent");
		for (Runnable task : startupTasks){
			task.run();
		}
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new MainModule());
		TinkerTimeLauncher launcher = injector.getInstance(TinkerTimeLauncher.class);
		launcher.run();
	}
}
