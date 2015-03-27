package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.workflows.contexts.ModDownloaderContext;

// TODO: Remove in favor of Event System
class MarkModUpdatedTask extends WorkflowTask {
	
	private static interface ModBuilder {
		Mod buildMod() throws IOException;
	}
	
	private final ModUpdateListener listener;
	private final ModBuilder modBuilder;
	private boolean deleted = false;

	private MarkModUpdatedTask(ModUpdateListener listener, ModBuilder builder) {
		super("Registering Mod as Updated");
		this.listener = listener;
		this.modBuilder = builder;
	}
	
	static MarkModUpdatedTask createFromDownloaderContext(ModUpdateListener listener, final ModDownloaderContext context){
		return new MarkModUpdatedTask(
			listener,
			new ModBuilder(){
				@Override
				public Mod buildMod() throws IOException {
					return context.createMod();
				}
			}
		);
	}
	
	static MarkModUpdatedTask createFromMod(ModUpdateListener listener, final Mod mod){
		return new MarkModUpdatedTask(
			listener,
			new ModBuilder(){
				@Override
				public Mod buildMod() throws IOException {
					return mod;
				}
			}
		);
	}
	
	static MarkModUpdatedTask notifyDeletion(ModUpdateListener listener, Mod mod, TinkerConfig config){
		MarkModUpdatedTask task = createFromMod(listener, mod);
		task.deleted = true;
		return task;
	}

	@Override
	public boolean execute() throws Exception {
		if (deleted){
			listener.modDeleted(modBuilder.buildMod());
		} else {
			listener.modUpdated(modBuilder.buildMod());
		}
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}
}