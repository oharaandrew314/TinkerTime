package aohara.tinkertime.views;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.DefaultEditorKit;

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
		// Try to use Nimbus Look & Feel
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		    
		    // Fix OSX key-bindings
		    if ( System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0){
		    	InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
			    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
			    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
			    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
			    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
		    }
		   
		} catch (Exception e) {}
		
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
