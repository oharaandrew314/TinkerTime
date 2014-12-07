package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.workflows.ModDownloaderContext;

public class MarkModUpdatedTask extends WorkflowTask {
	
	private static interface ModBuilder {
		Mod buildMod() throws IOException;
	}
	
	private final ModUpdateListener listener;
	private final ModBuilder modBuilder;
	private boolean deleted = false;

	private MarkModUpdatedTask(ModUpdateListener listener, ModBuilder builder) {
		this.listener = listener;
		this.modBuilder = builder;
	}
	
	public static MarkModUpdatedTask createFromDownloaderContext(ModUpdateListener listener, final ModDownloaderContext context){
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
	
	public static MarkModUpdatedTask createFromMod(ModUpdateListener listener, final Mod mod){
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
	
	public static MarkModUpdatedTask notifyDeletion(ModUpdateListener listener, Mod mod, TinkerConfig config){
		MarkModUpdatedTask task = createFromMod(listener, mod);
		task.deleted = true;
		return task;
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		if (deleted){
			listener.modDeleted(modBuilder.buildMod());
		} else {
			listener.modUpdated(modBuilder.buildMod());
		}
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return 0;
	}

	@Override
	public String getTitle() {
		return "Registering Mod as Updated";
	}
}