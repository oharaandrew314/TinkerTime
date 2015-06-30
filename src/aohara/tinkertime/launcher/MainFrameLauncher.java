package aohara.tinkertime.launcher;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import aohara.common.content.ImageManager;
import aohara.tinkertime.views.menus.MenuFactory;
import aohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import com.google.inject.Inject;

class MainFrameLauncher implements Runnable {

	private final MenuFactory menuFactory;
	private final ModSelectorPanelFactory selectorFactory;

	@Inject
	MainFrameLauncher(MenuFactory menuFactory, ModSelectorPanelFactory selectorFactory){
		this.menuFactory = menuFactory;
		this.selectorFactory = selectorFactory;
	}

	@Override
	public void run() {
		// Get App Icons
		ImageManager imageManager = new ImageManager();
		List<Image> appIcons = new ArrayList<Image>();
		appIcons.add(imageManager.getImage("icon/app/icon 128x128.png"));
		appIcons.add(imageManager.getImage("icon/app/icon 64x64.png"));
		appIcons.add(imageManager.getImage("icon/app/icon 32x32.png"));
		appIcons.add(imageManager.getImage("icon/app/icon 16x16.png"));

		// Initialize Frame
		JFrame frame = new JFrame(TinkerTimeLauncher.FULL_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setIconImages(appIcons);
		frame.setJMenuBar(menuFactory.createMenuBar());
		frame.add(menuFactory.createToolBar(), BorderLayout.NORTH);
		frame.add(selectorFactory.get().getComponent(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

}
