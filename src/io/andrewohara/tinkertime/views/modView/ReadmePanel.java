package io.andrewohara.tinkertime.views.modView;

import io.andrewohara.common.views.selectorPanel.SelectorView;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReadmePanel extends SelectorView.AbstractSelectorView<Mod> {

	private final JPanel panel = new JPanel();
	private JTextArea textArea;

	public ReadmePanel(){
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
		String readmeText = mod.getReadmeText();
		textArea.setText(readmeText);
		textArea.setCaretPosition(0);
		panel.setVisible(readmeText != null && !readmeText.trim().isEmpty());
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

}
