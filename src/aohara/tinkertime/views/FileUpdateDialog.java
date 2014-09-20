package aohara.tinkertime.views;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import thirdParty.VerticalLayout;

/**
 * Dialog which is used to update Files that are not considered "mods".
 * 
 * The Dialog shows the currently installed version, a button to check for
 * an updated version, and a button to download the latest version.  Once the
 * latest version has been retrieved with the "Check for Update" button, the
 * latest version name is displayed on the Dialog.
 * 
 * @author Andrew O'Hara
 */
@SuppressWarnings("serial")
public class FileUpdateDialog extends JDialog {
	
	private final JLabel currentVersionLabel, latestVersionLabel;
	
	public FileUpdateDialog(String name, Action downloadAction, Action checkAction){

		// Configure Dialog
		setTitle(name);
		setLayout(new VerticalLayout());
		setModal(true);
		setResizable(false);
		
		// Label Panel
		JPanel labelPanel = new JPanel(new VerticalLayout());
		labelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		labelPanel.add(latestVersionLabel = new JLabel("LatestVersion: Please Check"));
		labelPanel.add(currentVersionLabel = new JLabel());
		add(labelPanel);
		
		// Buttons
		add(new JButton(checkAction));
		add(new JButton(downloadAction));
		
		pack();
	}
	
	public void update(String currentVersion, String latestVersion){
		currentVersionLabel.setText(String.format(
			"Current Version: %s", currentVersion != null ? currentVersion : "Not Installed"
		));
		
		if (latestVersionLabel.getText() == null || latestVersion != null){
			latestVersionLabel.setText(String.format("Latest Version: %s", latestVersion));
		}
		
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(300, 160);
	}
}
