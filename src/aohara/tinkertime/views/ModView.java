package aohara.tinkertime.views;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import thirdParty.VerticalLayout;
import aohara.common.selectorPanel.SelectorView;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;

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
			JLabel pageLabel = new JLabel();
			pageLabel.setText("<html><a href=''>Go to Mod Page</a></html>");
			pageLabel.addMouseListener(new ModPageElement(pageLabel));
			panel.add(pageLabel);		
			
			// Description
			JEditorPane descrip = new JEditorPane("text/html", mod.getDescription());
			descrip.setEditable(false);
			descrip.addHyperlinkListener(this);
			panel.add(descrip);
			
			// Readme
			Config config = new Config();
			if (config.getModZipPath(mod).toFile().exists()){
				ModStructure struct = new ModStructure(mod, config);
				String readmeText = struct.getReadmeText();
				
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
	
	private class ModPageElement extends MouseAdapter{
		
		private ModPageElement(JComponent comp){
			comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			goToHyperlink(mod.getPageUrl());
		}
		
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			goToHyperlink(e.getURL());
        }
	}
	
	private void goToHyperlink(URL url){
		if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException | URISyntaxException e1) {
            	JOptionPane.showMessageDialog(
            		panel, "Could not open hyperlink:\n" + url);
            }
        }
	}
}
