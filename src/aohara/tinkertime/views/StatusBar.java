package aohara.tinkertime.views;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import aohara.common.executors.context.ExecutorContext;
import aohara.common.progressDialog.ProgressListener;
import aohara.common.selectorPanel.DecoratedComponent;

public class StatusBar<C extends ExecutorContext> implements ProgressListener<C>,
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
	public void progressStarted(C context, int target, int tasksRunning) {
		status.setText("Downloading " + tasksRunning);
	}

	@Override
	public void progressMade(C context, int current) {
		// No Response
	}

	@Override
	public void progressComplete(C context, int tasksRunning) {
		if (tasksRunning > 0){
			progressStarted(context, 0, tasksRunning);
		} else {
			status.setText("Idle");
		}
		
	}

	@Override
	public void progressError(ExecutorContext context, int tasksRunning) {
		status.setText("Download error for " + context);
		
	}
}
