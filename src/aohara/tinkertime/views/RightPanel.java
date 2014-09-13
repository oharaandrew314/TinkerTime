package aohara.tinkertime.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import aohara.common.selectorPanel.SelectorView;
import aohara.tinkertime.models.Mod;

public class RightPanel implements SelectorView<Mod, JPanel> {
	
	private final SelectorView<Mod, JPanel> modView;
	private final JPanel panel = new JPanel();
	
	public RightPanel(SelectorView<Mod, JPanel> modView, JPanel southPanel){
		this.modView = modView;
		panel.setLayout(new BorderLayout());
		panel.add(modView.getComponent(), BorderLayout.CENTER);
		panel.add(southPanel, BorderLayout.SOUTH);
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}

	@Override
	public void display(Mod element) {
		modView.display(element);
	}

	@Override
	public Mod getElement() {
		return modView.getElement();
	}

}
