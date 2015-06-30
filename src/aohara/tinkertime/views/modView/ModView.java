package aohara.tinkertime.views.modView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import aohara.common.views.selectorPanel.SelectorView;
import aohara.tinkertime.models.Mod;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Panel for displaying a Mod's information.
 *
 * Includes the Mod's file information, as well as the Readme if it exists.
 *
 * @author Andrew O'Hara
 */
@Singleton
public class ModView extends SelectorView.AbstractSelectorView<Mod> {

	private final JPanel panel = new JPanel();
	private final JLabel
	updateLabel = new JLabel(),
	modVersionLabel = new JLabel(),
	kspVersionLabel = new JLabel(),
	updatedOnLabel = new JLabel();
	private final ModImageView imageView;


	private final SelectorView<Mod> urlPanel, readmePanel;

	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	@Inject
	ModView(ModImageView imageView, ReadmePanel readmePanel){
		this.imageView = imageView;
		this.readmePanel = readmePanel;

		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new java.awt.Dimension(500, 600));

		JPanel topPanel = new JPanel(new BorderLayout());

		// Create Info Panel (Mod Info)
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(updateLabel);
		infoPanel.add(modVersionLabel);
		infoPanel.add(kspVersionLabel);
		infoPanel.add(updatedOnLabel);
		infoPanel.add((urlPanel = new ModUrlPanel()).getComponent());

		// Create Top Panel (Info Panel and Mod Image)
		topPanel.add(infoPanel, BorderLayout.CENTER);
		topPanel.add(imageView.getComponent(), BorderLayout.EAST);
		panel.add(topPanel, BorderLayout.NORTH);

		// Add Bottom (Readme) Panel
		readmePanel.getComponent().setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(readmePanel.getComponent(), BorderLayout.CENTER);
	}

	@Override
	protected void onElementChanged(Mod mod) {
		String versionString = mod.getModVersion() != null ? mod.getModVersion().toString() : "";

		if (mod != null){
			// Set Border
			panel.setBorder(BorderFactory.createTitledBorder(
					mod.getName() + versionString +
					(mod.isUpdateable()? " - by " + mod.getCreator() : " - added from zip"
							)));

			// Warning if non-updateable
			if (!mod.isUpdateable()){
				updateLabel.setText("<html><b>Warning:</b> Local File Only.  Not updateable.</html>");
			} else if(mod.isUpdateAvailable()) {
				updateLabel.setText("<html><b>An update for this mod is available.</b></html>");
			} else {
				updateLabel.setText(null);
			}

			// Current Mod Version
			modVersionLabel.setText(String.format("Mod Version: %s", versionString));
			modVersionLabel.setVisible(mod.getModVersion() != null);

			// Supported KSP Version
			String kspVersion =  mod.getSupportedVersion();
			kspVersionLabel.setText(String.format("KSP Version: %s", kspVersion != null ? mod.getSupportedVersion() : "Unknown"));

			// Last Updated On
			Date updatedOn = mod.getUpdatedOn();
			updatedOnLabel.setText("Last Updated: " + (updatedOn != null ? DATE_FORMAT.format(updatedOn) : "N/A"));

			// Mod Page Link
			urlPanel.display(mod);

			// Readme
			readmePanel.display(mod);

			// Image
			imageView.display(mod);
		}
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}
}
