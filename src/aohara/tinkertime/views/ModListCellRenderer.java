package aohara.tinkertime.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;

import thirdParty.CompoundIcon;
import aohara.common.Util;
import aohara.common.content.ImageManager;
import aohara.common.views.ProgressSpinnerPanel;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.common.workflows.tasks.WorkflowTask.TaskExceptionEvent;
import aohara.tinkertime.ModManager.ModNotDownloadedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;

/**
 * Custom ListCellRenderer for a Mod to be displayed on a JList.
 * 
 * Displays the Mod name, all status icons to the left of it, and the Progress
 * Spinner to the right.
 *  
 * @author Andrew O'Hara
 */
public class ModListCellRenderer extends TaskCallback implements ListCellRenderer<Mod> {
	
	private final ModLoader modLoader;
	private final ImageIcon enabledIcon, disabledIcon, errorIcon, updateIcon, localIcon;
	private final Map<Mod, ProgressSpinnerPanel> elements = new HashMap<>();
	
	private JList<? extends Mod> list;
	
	public ModListCellRenderer(
			ModLoader modLoader, ImageIcon checkIcon, ImageIcon xIcon,
			ImageIcon errorIcon, ImageIcon updateIcon, ImageIcon localIcon	
	){
		this.modLoader = modLoader;
		this.enabledIcon = checkIcon;
		this.disabledIcon = xIcon;
		this.errorIcon = errorIcon;
		this.updateIcon = updateIcon;
		this.localIcon = localIcon;		
	}
	
	public static ModListCellRenderer create(ModLoader modLoader){
		 ImageManager imageManager = new ImageManager("icon/");
		 return new ModListCellRenderer(
			 modLoader,
			 loadIcon(imageManager, "glyphicons_152_check.png", "Mod Enabled", new Color(70, 210, 70)),
			 loadIcon(imageManager, "glyphicons_207_remove_2.png", "Mod Disabled", new Color(205, 20, 20)),
			 loadIcon(imageManager, "glyphicons_078_warning_sign.png", "Mod Zip not found.  Please update", new Color(215, 160, 0)),
			 loadIcon(imageManager, "glyphicons_213_up_arrow.png", "Update Available", new Color(255, 200, 0)),
			 loadIcon(imageManager, "glyphicons_410_compressed.png", "Mod added locally.  Not updateable", new Color(0, 0, 0))
		);
		 
	}
	
	private static ImageIcon loadIcon(ImageManager imageManager, String name, String description, Color colour){
		BufferedImage image = imageManager.getImage(name);
		image = colour != null ? imageManager.colorize(image, colour) : image;
		return new ImageIcon(image, description);
	}
	
	private ImageIcon[] getCurrentIcons(Mod mod){
		LinkedList<ImageIcon> icons = new LinkedList<>();
		try {
			icons.add(modLoader.isEnabled(mod) ? enabledIcon : disabledIcon);
		} catch (ModNotDownloadedException e){
			icons.add(errorIcon);
		}
		
		if (mod.updateAvailable){
			icons.add(updateIcon);
		}
		
		if (mod.pageUrl == null){
			icons.add(localIcon);
		}
		return icons.toArray(new ImageIcon[0]);
	}
	
	public void startFramerateTimer(){
		new Timer(10, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list != null){
					list.repaint();
				}
			}
		}).start();
	}

	@Override
	public Component getListCellRendererComponent(final JList<? extends Mod> list,
			Mod mod, int index, boolean isSelected, boolean cellHasFocus) {
		
		this.list = list;
		
		// Compile list of icons
		ImageIcon[] icons = getCurrentIcons(mod);
		
		// Create cell label
		String text = mod.name;
		if (mod.getSupportedVersion() != null){
			text = String.format("[%s] %s", mod.getSupportedVersion(), text);
		}
		
		String tooltipText = String.format("<html>%s</html>", Util.joinStrings(icons, "<br/>"));
		
		if (!elements.containsKey(mod)){
			elements.put(mod, ProgressSpinnerPanel.create());
		}
		ProgressSpinnerPanel ele = elements.get(mod);
		ele.setText(text);
		ele.setIcon(new CompoundIcon(icons));
		ele.setBorder(isSelected ? BorderFactory.createLineBorder(Color.black) : null);
		ele.setToolTipText(tooltipText);
		return ele;
	}
	
	@Override
	protected void processTaskEvent(TaskEvent event) {
		Object context  = event.getTask().getWorkflow().context;
		if (context == null || !elements.containsKey(context)){
			return;
		}
		
		ProgressSpinnerPanel element = elements.get(context);
		
		switch(event.getTask().getStatus()){
		case Ready:
			break;  // Ignored
		case Running:
			if (!element.isRunning()){
				element.start();
				new Runnable(){
					@Override
					public void run(){
						element.setMaxProgress(event.getTask().getTargetProgress());
					}
				}.run();
			} else {
				element.setProgress(event.getTask().getProgress());
			}
			break;
		case Exception:
			JOptionPane.showMessageDialog(
				element,
				"An error ocurred while processing:\n" + event.getTask().getWorkflow() +
				"\n\n" +
				((TaskExceptionEvent)event).exception,
				"Error!",
				JOptionPane.ERROR_MESSAGE
			);
		case Success:
		case Failure:
			element.reset();
			break;
		}
	}
}
