package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AnalyzeModZipTask extends WorkflowTask {

	private final Mod mod;
	private final ModUpdateCoordinator modUpdateCoordinator;

	public AnalyzeModZipTask(Mod mod, ModUpdateCoordinator modUpdateCoordinator) {
		super("Analyzing Mod");
		this.mod = mod;
		this.modUpdateCoordinator = modUpdateCoordinator;
	}

	@Override
	public boolean execute() throws Exception {
		Collection<ModFile> files = new LinkedList<>();

		Path zipPath = mod.getZipPath();
		if (zipPath == null){
			throw new FileNotFoundException();
		} else if(!zipPath.toFile().exists()){
			throw new FileNotFoundException(zipPath.toString());
		}

		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			Path gameDataPath = null;

			//Make first pass of entries to get key information
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			for (ZipEntry entry; entries.hasMoreElements(); ){
				entry = entries.nextElement();

				// Find Gamedata path
				if (gameDataPath == null && entry.getName().toLowerCase().contains("gamedata")){

					// Once a candidate has been found, find the exact path to the folder
					Path tempPath = Paths.get(entry.getName());
					while(tempPath != null && gameDataPath == null){
						if (tempPath.getFileName().toString().toLowerCase().equals("gamedata")){
							gameDataPath = tempPath;
						} else {
							tempPath = tempPath.getParent();
						}
					}
				}

				/* TODO reimplement readmeText
				// Find Readme text
				if (readmeText == null && !entry.isDirectory() && entry.getName().toLowerCase().contains("readme")){
					try(StringWriter writer = new StringWriter(); InputStream is = zipFile.getInputStream(entry)){
						IOUtils.copy(is, writer);
						readmeText = writer.toString();
					} catch (IOException e) {}
				}
				 */
			}

			// Make second pass of entries to get all Mod Files
			entries = zipFile.entries();
			if (gameDataPath == null){
				// If no gameDataPath, get all files with a path length of at least 2.
				// This is because, we only get files which are within folders in the root of the zip
				for (ZipEntry entry; entries.hasMoreElements(); ){
					entry = entries.nextElement();
					Path entryPath = Paths.get(entry.getName());
					if (!entry.isDirectory() && entryPath.getNameCount() >= 2){
						files.add(new ModFile(mod, entry.getName(), entryPath));
					}
				}
			} else {
				// Get all files within the GameData directory
				for (ZipEntry entry; entries.hasMoreElements(); ){
					entry = entries.nextElement();
					Path entryPath = Paths.get(entry.getName());
					if (
							!entry.isDirectory() &&
							entryPath.startsWith(gameDataPath) && !entryPath.equals(gameDataPath) &&
							!(entry.getName().contains("ModuleManager") && entry.getName().endsWith(".dll"))
							){
						files.add(new ModFile(mod, entry.getName(), gameDataPath.relativize(entryPath)));
					}
				}
			}

			// If no files found, make third pass to get DLLs in root
			if (files.isEmpty()){
				entries = zipFile.entries();
				for (ZipEntry entry; entries.hasMoreElements(); ){
					entry = entries.nextElement();
					if (entry.getName().endsWith(".dll")){
						Path entryPath = Paths.get(entry.getName());
						Path path = gameDataPath == null ? entryPath : gameDataPath.relativize(entryPath);
						files.add(new ModFile(mod, entry.getName(), path));
					}
				}
			}
		}

		modUpdateCoordinator.updateModFiles(mod, files);
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}
}
