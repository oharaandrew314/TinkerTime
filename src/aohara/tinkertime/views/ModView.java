package aohara.tinkertime.views;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import aohara.common.config.views.ReadmePanel;
import aohara.common.selectorPanel.SelectorView;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;

/**
 * Panel for displaying a Mod's information.
 * 
 * Includes the Mod's file information, as well as the Readme if it exists.
 * 
 * @author Andrew O'Hara
 */
public class ModView extends SelectorView.AbstractSelectorView<Mod> {
	
	private final JPanel panel = new JPanel();
	private final JLabel
		updateLabel = new JLabel(),
		modVersionLabel = new JLabel(),
		kspVersionLabel = new JLabel(),
		updatedOnLabel = new JLabel();
	
	private final SelectorView<Mod> urlPanel, readmePanel;
	
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	
	public ModView(final ModLoader modLoader){		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new java.awt.Dimension(400, 600));
		
		updateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		modVersionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		kspVersionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		updatedOnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		panel.add(updateLabel);		
		panel.add(modVersionLabel);
		panel.add(kspVersionLabel);
		panel.add(updatedOnLabel);
		
		urlPanel = new ModUrlPanel();
		urlPanel.getComponent().setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(urlPanel.getComponent());
		
		readmePanel = new ReadmePanel(modLoader);
		readmePanel.getComponent().setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(readmePanel.getComponent());
	}

	@Override
	protected void onElementChanged(Mod mod) {		
		String versionString = mod.getVersion() != null ? " v" + mod.getVersion() : "";
		
		if (mod != null){
			// Set Border
			panel.setBorder(BorderFactory.createTitledBorder(
				mod.name + versionString +
				(mod.isUpdateable()? " - by " + mod.creator : " - added from zip"
			)));
			
			// Warning if non-updateable
			if (!mod.isUpdateable()){
				updateLabel.setText("<html><b>Warning:</b> Local File Only.  Not updateable.</html>");
			} else if(mod.updateAvailable) {
				updateLabel.setText("<html><b>An update for this mod is available.</b></html>");
			}
			
			// Current Mod Version
			modVersionLabel.setText(String.format("Mod Version: %s", versionString));
			modVersionLabel.setVisible(mod.getVersion() != null);
			
			// Supported KSP Version
			String kspVersion =  mod.getSupportedVersion();
			kspVersionLabel.setText(String.format("KSP Version: %s", kspVersion != null ? mod.getSupportedVersion() : "Unknown"));
			
			// Last Updated On
			Date updatedOn = mod.updatedOn;
			updatedOnLabel.setText("Last Updated: " + (updatedOn != null ? DATE_FORMAT.format(updatedOn) : "N/A"));

			// Mod Page Link
			urlPanel.display(mod);
			
			// Readme
			readmePanel.display(mod);
		}
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}
}
