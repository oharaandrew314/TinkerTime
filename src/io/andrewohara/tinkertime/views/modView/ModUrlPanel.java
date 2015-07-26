package io.andrewohara.tinkertime.views.modView;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JLabel;

import io.andrewohara.common.Util;
import io.andrewohara.common.views.Dialogs;
import io.andrewohara.common.views.selectorPanel.SelectorView;
import io.andrewohara.tinkertime.models.mod.Mod;

/**
 * Decorated JLabel which contains a clickable URL.
 *
 * If the URL is clicked, the page will be loaded using system's default browser.
 *
 * @author Andrew O'Hara
 */
public class ModUrlPanel extends SelectorView.AbstractSelectorView<Mod> {

	private static final String LABEL_TEMPLATE = "<html><a href='%s'>%s</a></html>";

	private final JLabel label = new JLabel();

	public ModUrlPanel(final Dialogs dialogs){
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Util.goToHyperlink(getElement().getUrl());
				} catch (IOException e1) {
					dialogs.errorDialog(label, e1);
				}
			}
		});
	}

	@Override
	public JLabel getComponent() {
		return label;
	}

	@Override
	protected void onElementChanged(Mod mod) {
		if (mod.isUpdateable()){
			String previewText = String.format(
					"Go to Mod Page (on %s)",
					mod.getUrl().getHost()
					);

			label.setText(String.format(
					LABEL_TEMPLATE,
					mod.getUrl(),
					previewText
					));
		}
		label.setVisible(mod.isUpdateable());
	}

}
