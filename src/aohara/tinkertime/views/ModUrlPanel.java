package aohara.tinkertime.views;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JLabel;

import aohara.common.Util;
import aohara.common.selectorPanel.SelectorView;
import aohara.tinkertime.models.Mod;

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

	public ModUrlPanel(){
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Util.goToHyperlink(getElement().pageUrl);
				} catch (IOException e1) {
					Dialogs.errorDialog(label, e1);
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
				mod.pageUrl.getHost()
			);
			
			label.setText(String.format(
				LABEL_TEMPLATE,
				mod.pageUrl,
				previewText
			));
		}
		label.setVisible(mod.isUpdateable());
	}

}
