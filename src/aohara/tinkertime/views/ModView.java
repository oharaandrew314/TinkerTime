package aohara.tinkertime.views;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import thirdParty.VerticalLayout;
import aohara.common.Util;
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
public class ModView implements SelectorView<Mod, JPanel>, HyperlinkListener {
	
	private final ModLoader modLoader;
	private Mod mod;
	private final JPanel panel = new JPanel();
	private final SimpleDateFormat DATE_FORMAT = (
			new SimpleDateFormat("yyyy/MM/dd"));
	
	public ModView(final ModLoader modLoader){
		this.modLoader = modLoader;
		panel.setLayout(new VerticalLayout(0, VerticalLayout.BOTH));
	}

	@Override
	public void display(Mod mod) {
		this.mod = mod;
		panel.removeAll();
		
		String versionString = mod.getVersion() != null ? " v" + mod.getVersion() : "";
		
		if (mod != null){
			// Set Border
			panel.setBorder(BorderFactory.createTitledBorder(
				mod.name + versionString +
				(mod.isUpdateable()? " - by " + mod.creator : " - added from zip"
			)));
			
			// Warning if non-updateable
			if (!mod.isUpdateable()){
				panel.add(new JLabel("<html><b>Warning:</b> Local File Only.  Not updateable.</html>"));
			} else if(mod.updateAvailable) {
				panel.add(new JLabel("<html><b>An update for this mod is available.</b></html>"));
			}
			
			// Current Mod Version
			if (mod.getVersion() != null){
				panel.add(new JLabel("Mod Version: " + versionString));
			}
			
			// Supported KSP Version
			String kspVersion =  mod.getSupportedVersion();
			panel.add(new JLabel("KSP Version: " + (kspVersion != null ? kspVersion : "Unknown")));
			
			// Last Updated On
			JLabel updatedLabel = new JLabel();
			Date updatedOn = mod.updatedOn;
			updatedLabel.setText("Last Updated: " + (updatedOn != null ? DATE_FORMAT.format(updatedOn) : "N/A"));
			panel.add(updatedLabel);

			// Mod Page Link
			panel.add(new UrlPanel(				
				String.format(
					"Go to Mod Page (on %s)",
					mod.isUpdateable() ? mod.pageUrl.getHost() : null
				),
				mod.pageUrl
			).getComponent());

			// Readme
			try{
				String readmeText = modLoader.getStructure(mod).getReadmeText();
				if (readmeText != null && !readmeText.trim().isEmpty()){
					panel.add(new JLabel("<html><b>Readme:</b></html"));
					JTextArea readmeArea = new JTextArea(readmeText);
					readmeArea.setLineWrap(true);
					readmeArea.setWrapStyleWord(true);
					readmeArea.setEditable(false);
					panel.add(readmeArea);
				}
			} catch(IOException e){
				// Do Nothing
			}

		}
	}

	@Override
	public Mod getElement() {
		return mod;
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}

	// -- Listeners ---------------------------------------------------------

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
	            Util.goToHyperlink(e.getURL());
	        } catch (IOException e1) {
	        	JOptionPane.showMessageDialog(
	        		panel, "Could not open hyperlink:\n" + e.getURL());
	        }
        }
	}
}
