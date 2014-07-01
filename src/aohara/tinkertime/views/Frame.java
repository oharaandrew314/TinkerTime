package aohara.tinkertime.views;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import aoahara.common.selectorPanel.DecoratedComponent;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.ModManager;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	
	public Frame(ModManager mm, DecoratedComponent<?> selectorPanel, DecoratedComponent<?> statusBar){
		setTitle(TinkerTime.NAME);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setJMenuBar(new TinkerMenuBar(this, mm));
		
		add(selectorPanel.getComponent(), BorderLayout.CENTER);
		add(statusBar.getComponent(), BorderLayout.SOUTH);
		setVisible(true);
	}
	
	

}
