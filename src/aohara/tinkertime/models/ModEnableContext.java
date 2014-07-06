package aohara.tinkertime.models;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import aohara.common.executors.context.ExecutorContext;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.ModStructure.Module;

public class ModEnableContext extends ExecutorContext {
	
	public final Mod mod;
	private final Config config;
	public final ModStructure struct;
	public final EnableAction action;
	
	public static enum EnableAction { Enable, Disable, Delete };
	
	public ModEnableContext(Mod mod, ModStructure struct, Config config, EnableAction action){
		this.mod = mod;
		this.config = config;
		this.struct = struct;
		this.action = action;
	}

	@Override
	public String toString() {
		return "Enabling " + mod.getName();
	}

	@Override
	public int getTotalProgress() {
		int total = 0;
		for (Module module : struct.getModules()){
			total += getModuleSize(module);
		}
		return total;
	}
	
	public int getModuleSize(Module module){
		int total = 0;
		for (ZipEntry entry : module.getEntries()){
			total += entry.getSize();
		}
		return total;
	}
	
	public Path getGameDataPath(){
		return config.getGameDataPath();
	}
	
	public ZipFile getZipFile() throws ZipException, IOException{
		return new ZipFile(struct.zipPath.toFile());
	}
}
