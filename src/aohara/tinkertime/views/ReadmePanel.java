package aohara.tinkertime.views;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import aohara.common.selectorPanel.SelectorView;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;

public class ReadmePanel extends SelectorView.AbstractSelectorView<Mod> {
	
	private final JPanel panel = new JPanel();
	private final ModLoader modLoader;
	private JTextArea textArea;
	
	public ReadmePanel(ModLoader modLoader){
		this.modLoader = modLoader;
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		
		panel.setVisible(false);
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel("<html><b>Readme:</b></html"), BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	protected void onElementChanged(Mod mod) {
		try {
			String readmeText = modLoader.getStructure(mod).getReadmeText();
			textArea.setText(readmeText);
			textArea.setCaretPosition(0);
			panel.setVisible(readmeText != null && !readmeText.trim().isEmpty());
		} catch (IOException e) {
			panel.setVisible(false);
		}
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

}
