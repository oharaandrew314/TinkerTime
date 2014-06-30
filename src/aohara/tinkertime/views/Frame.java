package aohara.tinkertime.views;

import javax.swing.JFrame;

import aoahara.common.selectorPanel.SelectorPanel;
import aohara.tinkertime.models.Mod;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	
	public Frame(SelectorPanel<Mod> selectorPanel){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		
		add(selectorPanel);
		
		setVisible(true);
	}
	
	

}
