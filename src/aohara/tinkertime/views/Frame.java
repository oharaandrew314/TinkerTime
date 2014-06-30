package aohara.tinkertime.views;

import javax.swing.JFrame;

import aoahara.common.selectorPanel.SelectorPanel;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.models.Mod;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	
	public Frame(SelectorPanel<Mod> selectorPanel, ModManager mm){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setJMenuBar(new TinkerMenuBar(this, mm));
		
		add(selectorPanel);
		
		setVisible(true);
	}
	
	

}
