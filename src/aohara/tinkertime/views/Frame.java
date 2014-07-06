package aohara.tinkertime.views;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import aohara.common.selectorPanel.DecoratedComponent;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.ModManager;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	
	public Frame(ModManager mm, DecoratedComponent<?> selectorPanel,
			DecoratedComponent<?> statusBar, JMenuBar menuBar){
		setTitle(TinkerTime.NAME);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setJMenuBar(menuBar);
		
		// Add App icons
		ArrayList<Image> imageList = new ArrayList<Image>();
		imageList.add(loadIcon("icon 128x128.png"));
		imageList.add(loadIcon("icon 64x64.png"));
		imageList.add(loadIcon("icon 32x32.png"));
		imageList.add(loadIcon("icon 16x16.png"));
		setIconImages(imageList);
		
		add(selectorPanel.getComponent(), BorderLayout.CENTER);
		add(statusBar.getComponent(), BorderLayout.SOUTH);
		setVisible(true);
	}
	
	private Image loadIcon(String name){
		try {
			URL url = getClass().getClassLoader().getResource("icon/app/" + name);
			return ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

}
