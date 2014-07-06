package aohara.tinkertime;

import javax.swing.JOptionPane;

import aohara.common.executors.Downloader;
import aohara.common.executors.FileTransferExecutor.FileConflictResolver;
import aohara.common.executors.TempDownloader;
import aohara.common.executors.context.FileTransferContext;
import aohara.common.progressDialog.ProgressDialog;
import aohara.common.selectorPanel.ListListener;
import aohara.common.selectorPanel.SelectorPanel;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.CannotDisableModException;
import aohara.tinkertime.controllers.ModManager.CannotEnableModException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyEnabledException;
import aohara.tinkertime.controllers.ModManager.ModNotDownloadedException;
import aohara.tinkertime.controllers.DownloaderManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.DialogConflictResolver;
import aohara.tinkertime.views.Frame;
import aohara.tinkertime.views.ModImageView;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.StatusBar;
import aohara.tinkertime.views.TinkerMenuBar;

public class TinkerTime implements ListListener<Mod> {
	
	public static final String NAME = "Tinker Time";
	private final ModManager mm;
	
	public TinkerTime(){
		Config.verifyConfig();
		
		// Initialize Controllers
		Downloader downloader = new TempDownloader(ModManager.NUM_CONCURRENT_DOWNLOADS, FileConflictResolver.Overwrite);
		ProgressDialog<FileTransferContext> progressDialog = new ProgressDialog<>("Downloads Mods");
		
		Config config = new Config();
		ModStateManager sm = new ModStateManager(config.getModsPath().resolve("mods.json"));
		DownloaderManager dm = new DownloaderManager(sm, downloader, config);
		mm = new ModManager(sm, config, new DialogConflictResolver(config, sm), downloader);
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView());
		sp.addControlPanel(true, new ModImageView());
		sp.setListCellRenderer(new ModListCellRenderer());
		StatusBar<FileTransferContext> statusBar = new StatusBar<>();
		TinkerMenuBar menuBar = new TinkerMenuBar(mm, dm, sm);		
		
		// Add Listeners
		sp.addListener(this);
		sp.addListener(menuBar);
		downloader.addListener(statusBar);
		downloader.addListener(progressDialog);
		sm.addListener(sp);

		// Start Application
		sm.getMods();  // Load mods (will notify selector panel)
		new Frame(mm, sp, statusBar, menuBar);
	}
	
	public static void main(String[] args) {
		new TinkerTime();
	}

	@Override
	public void elementClicked(Mod mod, int numTimes) {
		if (numTimes == 2){
			if (mod.isEnabled()){
				try {
					mm.disableMod(mod);
				} catch (ModAlreadyDisabledException
						| CannotDisableModException e) {
					errorMessage("Could not disable " + mod.getName());
				}
			} else {
				try {
					mm.enableMod(mod);
				} catch (ModAlreadyEnabledException | ModNotDownloadedException
						| CannotEnableModException | CannotDisableModException e) {
					errorMessage("Could not enable " + mod.getName());
				}
			}
		}
		
	}
	
	private void errorMessage(String message){
		JOptionPane.showMessageDialog(
			null, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void elementSelected(Mod element) {
		// Do Nothing
	}

}
