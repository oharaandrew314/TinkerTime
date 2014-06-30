package aohara.tinkertime.views;

import javax.swing.JLabel;

import aoahara.common.selectorPanel.SelectorView;
import aohara.tinkertime.models.Mod;

@SuppressWarnings("serial")
public class ModView extends SelectorView<Mod> {
	
	private final JLabel label = new JLabel();
	
	public ModView(){
		add(label);
	}

	@Override
	public void display(Mod mod) {
		if (mod != null){
			label.setText(mod.getName());
		}
	}

}
