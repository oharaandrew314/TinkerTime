package aohara.tinkertime.views;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JFrame;

import aohara.common.content.ImageManager;
import aohara.tinkertime.TinkerTime;

/**
 * The main Application JFrame.
 *
 * @author Andrew O'Hara
 */
@SuppressWarnings("serial")
public class TinkerFrame extends JFrame {
	
	public TinkerFrame() {
		
		setTitle(TinkerTime.NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// Add App icons
		ArrayList<Image> imageList = new ArrayList<Image>();
		ImageManager imageManager = new ImageManager();
		imageList.add(imageManager.getImage("icon/app/icon 128x128.png"));
		imageList.add(imageManager.getImage("icon/app/icon 64x64.png"));
		imageList.add(imageManager.getImage("icon/app/icon 32x32.png"));
		imageList.add(imageManager.getImage("icon/app/icon 16x16.png"));
		setIconImages(imageList);
	}
}
