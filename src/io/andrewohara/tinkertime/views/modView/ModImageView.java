package io.andrewohara.tinkertime.views.modView;

import io.andrewohara.common.views.selectorPanel.SelectorView;
import io.andrewohara.tinkertime.models.ModImage;
import io.andrewohara.tinkertime.models.mod.Mod;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.google.inject.Inject;

/**
 * Component which displays the Mod's image from a given URL.
 *
 * @author Andrew O'Hara
 */
public class ModImageView extends SelectorView.AbstractSelectorView<Mod> {

	private final JLabel label = new JLabel();

	@Inject
	ModImageView(){
		label.setMaximumSize(ModImage.MAX_SIZE);
	}

	@Override
	public JComponent getComponent() {
		return label;
	}

	@Override
	protected void onElementChanged(Mod element) {
		try {
			if (element == null || element.getImage() == null){
				throw new NoModImageException();
			}
			label.setIcon(new ImageIcon(element.getImage().getImage()));
		} catch (NoModImageException e) {
			label.setIcon(null);
		}
	}

	@SuppressWarnings("serial")
	private static class NoModImageException extends Exception {}
}
