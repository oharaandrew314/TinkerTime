package aohara.tinkertime.resources;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import aohara.common.content.ImageManager;

public class Icons {
	
	private static final ImageManager imageManager = new ImageManager();
	
	public static List<Image> getAppIcons(){
		List<Image> imageList = new ArrayList<Image>();
		imageList.add(imageManager.getImage("icon/app/icon 128x128.png"));
		imageList.add(imageManager.getImage("icon/app/icon 64x64.png"));
		imageList.add(imageManager.getImage("icon/app/icon 32x32.png"));
		imageList.add(imageManager.getImage("icon/app/icon 16x16.png"));
		return imageList;
	}

}
