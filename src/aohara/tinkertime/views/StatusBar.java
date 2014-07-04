package aohara.tinkertime.views;

import java.awt.FlowLayout;
import java.nio.file.Path;

import javax.swing.JLabel;
import javax.swing.JPanel;

import aohara.common.progressDialog.ProgressListener;
import aohara.common.selectorPanel.DecoratedComponent;

public class StatusBar implements ProgressListener<Path>,
		DecoratedComponent<JPanel> {
	
	private JPanel panel = new JPanel();
	private JLabel status = new JLabel();
	
	public StatusBar(){
		status.setText("Idle");
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));
		panel.add(status);
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}

	@Override
	public void progressStarted(Path object, int target, int tasksRunning) {
		status.setText("Downloading " + tasksRunning);
	}

	@Override
	public void progressMade(Path object, int current) {
		// No Response
	}

	@Override
	public void progressComplete(Path object, int tasksRunning) {
		if (tasksRunning > 0){
			progressStarted(object, 0, tasksRunning);
		} else {
			status.setText("Idle");
		}
		
	}

	@Override
	public void progressError(Path object, int tasksRunning) {
		status.setText("Download error for " + object);
		
	}
}
