package aohara.tinkertime.views;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import aohara.common.selectorPanel.DecoratedComponent;
import aohara.tinkertime.controllers.ModDownloadListener;
import aohara.tinkertime.models.ModApi;

public class StatusBar implements ModDownloadListener, DecoratedComponent<JPanel> {
	
	private JPanel panel = new JPanel();
	private JLabel status = new JLabel();
	
	public StatusBar(){
		status.setText("Idle");
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));
		panel.add(status);
	}

	@Override
	public void modDownloadStarted(ModApi mod, int numMods) {
		status.setText("Downloading " + numMods);
		
	}

	@Override
	public void modDownloadComplete(ModApi mod, int numMods) {
		if (numMods > 0){
			modDownloadStarted(mod, numMods);
		} else {
			status.setText("Idle");
		}
	}

	@Override
	public void modDownloadError(ModApi mod, int numMods) {
		status.setText("Download error for " + mod.getNewestFile());
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}
}
