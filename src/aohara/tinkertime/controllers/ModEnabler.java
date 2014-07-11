package aohara.tinkertime.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import aohara.common.executors.ProgressExecutor;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModManager.CannotDisableModException;
import aohara.tinkertime.controllers.ModManager.CannotEnableModException;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.controllers.files.ConflictResolver.Resolution;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModEnableContext;
import aohara.tinkertime.models.ModEnableContext.EnableAction;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class ModEnabler extends ProgressExecutor<ModEnableContext> {
	
	public static final int NUM_PARALLEL_UNZIPS = 1;
	private final ConflictResolver cr;
	
	public ModEnabler(ConflictResolver cr) {
		super(NUM_PARALLEL_UNZIPS);
		this.cr = cr;
	}
	
	public void enable(Mod mod, Config config){
		submit(mod, config, EnableAction.Enable);
	}
	
	public void disable(Mod mod, Config config){
		submit(mod, config, EnableAction.Disable);
	}
	
	public void delete(Mod mod, Config config){
		submit(mod, config, EnableAction.Delete);
	}
	
	private void submit(Mod mod, Config config, EnableAction action){
		ModStructure struct = new ModStructure(mod, config);
		submit(new EnablerTask(new ModEnableContext(mod, struct, config, action)));
	}
	
	// -- Task ---------------------------------------------------------

	private class EnablerTask extends ExecutorTask {

		protected EnablerTask(ModEnableContext context) {
			super(context);
		}

		@Override
		protected void execute(ModEnableContext context)
				throws CannotDisableModException, CannotEnableModException {
			for (Module module : context.struct.getModules()){
				
				// Process Module
				switch(context.action){
				case Enable: tryEnableModule(context, module); break;
				case Disable: tryDisableModule(context, module); break;
				case Delete:
					tryDisableModule(context, module);
					FileUtils.deleteQuietly(context.struct.zipPath.toFile()); break;
				default: throw new IllegalStateException();
				}
			}
		}
		
		private void tryEnableModule(ModEnableContext context, Module module)
				throws CannotDisableModException, CannotEnableModException{
			// Process Conflict if necessary
			if (getDestPath(context, module).toFile().exists()){
				Resolution res = cr.getResolution(module, context.mod);
				if (res.equals(Resolution.Overwrite)){
					forceDisableModule(context, module);
					forceEnableModule(context, module);
				} else if (res.equals(Resolution.Skip)){
					// Skip Module
					progress(context.getModuleSize(module));
				} else {
					throw new IllegalStateException("Uncaught Resolution");
				}
			}
			//  Otherwise, no Conflict, so just enable
			else {
				forceEnableModule(context, module);
			}
		}
		
		private void tryDisableModule(ModEnableContext context, Module module) throws CannotDisableModException{
			// Only disable module if there are no other dependencies on it
			if (cr.getDependentMods(module).size() == 1){
				forceDisableModule(context, module);
			}
		}
		
		private Path getDestPath(ModEnableContext context, Module module){
			return context.getGameDataPath().resolve(module.getName());
		}
		
		private void forceEnableModule(ModEnableContext context, Module module)
				throws CannotEnableModException{
			try {
				try (ZipFile zipFile = new ZipFile(context.struct.zipPath.toFile())) {
					for (ZipEntry entry : module.getEntries()) {
						transfer(
							zipFile.getInputStream(entry),
							context.getGameDataPath().resolve(entry.getName())
						);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new CannotEnableModException();
			}
		}
		
		private final void transfer(InputStream is, Path dest) throws IOException {
			dest.getParent().toFile().mkdirs();
			try (
				OutputStream os = new FileOutputStream(dest.toFile());
			){
				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = is.read(buf)) > 0) {
					os.write(buf, 0, bytesRead);
					progress(bytesRead);
				}
			} finally {
				is.close();
			}
		}
		
		private void forceDisableModule(ModEnableContext context, Module module) throws CannotDisableModException{			
			try {
				FileUtils.deleteDirectory(getDestPath(context, module).toFile());
			} catch (IOException e) {
				throw new CannotDisableModException();
			}
		}

		@Override
		protected int getTotalProgress(ModEnableContext context) {
			int total = 0;
			for (Module module : context.struct.getModules()){
				total += context.getModuleSize(module);
			}
			return total;
		}
	}
}
