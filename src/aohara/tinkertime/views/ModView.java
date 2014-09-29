package aohara.tinkertime.views;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

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
import aohara.tinkertime.Config;
import aohara.tinkertime.content.ArchiveInspector;
import aohara.tinkertime.models.Mod;

/**
 * Panel for displaying a Mod's information.
 * 
 * Includes the Mod's file information, as well as the Readme if it exists.
 * 
 * @author Andrew O'Hara
 */
public class ModView implements SelectorView<Mod, JPanel>, HyperlinkListener {
	
	private Mod mod;
	private final JPanel panel = new JPanel();
	private final SimpleDateFormat DATE_FORMAT = (
			new SimpleDateFormat("yyyy/MM/dd"));
	
	public ModView(){
		panel.setLayout(new VerticalLayout(0, VerticalLayout.BOTH));
	}

	@Override
	public void display(Mod mod) {
		this.mod = mod;
		panel.removeAll();
		
		if (mod != null){
			// Set Border
			panel.setBorder(BorderFactory.createTitledBorder(mod.getName() + " - by " + mod.getCreator()));
			
			JLabel updatedLabel = new JLabel();
			updatedLabel.setText("Last Updated: " + DATE_FORMAT.format(mod.getUpdatedOn()));
			panel.add(updatedLabel);
			
			// Mod Page Link
			panel.add(new UrlPanel("Go to Mod Page", mod.getPageUrl()).getComponent());		
			
			// Readme
			Config config = new Config();
			if (mod.getCachedZipPath(config).toFile().exists()){
				String readmeText = ArchiveInspector.getReadmeText(config, mod);				
				if (readmeText != null && !readmeText.trim().isEmpty()){
					panel.add(new JLabel("<html><b>Readme:</b></html"));
					JTextArea readmeArea = new JTextArea(readmeText);
					readmeArea.setLineWrap(true);
					readmeArea.setWrapStyleWord(true);
					readmeArea.setEditable(false);
					panel.add(readmeArea);
				}
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
			goToHyperlink(e.getURL());
        }
	}
	
	private void goToHyperlink(URL url){
        try {
            Util.goToHyperlink(url);
        } catch (IOException e1) {
        	JOptionPane.showMessageDialog(
        		panel, "Could not open hyperlink:\n" + url);
        }
	}
}
