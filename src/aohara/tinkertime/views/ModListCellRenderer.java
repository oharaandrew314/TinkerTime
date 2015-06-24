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
import javax.swing.ListCellRenderer;
import javax.swing.Timer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import thirdParty.CompoundIcon;
import aohara.common.Util;
import aohara.common.content.ImageManager;
import aohara.common.views.Dialogs;
import aohara.common.views.ProgressSpinnerPanel;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.common.workflows.tasks.WorkflowTask.TaskExceptionEvent;
import aohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModMetaLoader;

/**
 * Custom ListCellRenderer for a Mod to be displayed on a JList.
 * 
 * Displays the Mod name, all status icons to the left of it, and the Progress
 * Spinner to the right.
 *  
 * @author Andrew O'Hara
 */
@Singleton
public class ModListCellRenderer extends TaskCallback implements ListCellRenderer<Mod> {
	
	private final Map<Mod, ProgressSpinnerPanel> elements = new HashMap<>();
	private final ModMetaLoader modLoader;
	private final ImageManager imageManager;
	private JList<? extends Mod> list;
	
	@Inject
	ModListCellRenderer(ImageManager imageManager, ModMetaLoader modLoader){
		this.imageManager = imageManager;
		this.modLoader = modLoader;
	}
	
	protected ImageIcon getEnabledIcon(){
		return loadIcon(imageManager, "glyphicons_152_check.png", "Mod Enabled", new Color(70, 210, 70));
	}
	
	protected ImageIcon getDisabledIcon(){
		return  loadIcon(imageManager, "glyphicons_207_remove_2.png", "Mod Disabled", new Color(205, 20, 20));
	}
	
	protected ImageIcon getErrorIcon(){
		return loadIcon(imageManager, "glyphicons_078_warning_sign.png", "Mod Zip not found.  Please update", new Color(215, 160, 0));
	}
	
	protected ImageIcon getUpdateIcon(){
		return loadIcon(imageManager, "glyphicons_213_up_arrow.png", "Update Available", new Color(255, 200, 0));
	}
	
	protected ImageIcon getLocalIcon(){
		return loadIcon(imageManager, "glyphicons_410_compressed.png", "Mod added locally.  Not updateable", new Color(0, 0, 0));
	}
	
	private ImageIcon loadIcon(ImageManager imageManager, String name, String description, Color colour){
		BufferedImage image = imageManager.getImage("icon/" + name);
		image = colour != null ? imageManager.colorize(image, colour) : image;
		return new ImageIcon(image, description);
	}
	
	private ImageIcon[] getCurrentIcons(Mod mod){
		LinkedList<ImageIcon> icons = new LinkedList<>();
		try {
			icons.add(modLoader.isEnabled(mod) ? getEnabledIcon() : getDisabledIcon());
		} catch (ModNotDownloadedException e){
			icons.add(getErrorIcon());
		}
		
		if (mod.updateAvailable){
			icons.add(getUpdateIcon());
		}
		
		if (mod.pageUrl == null){
			icons.add(getLocalIcon());
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
	protected void processTaskEvent(final TaskEvent event) {
		Object context  = event.getTask().getWorkflow().context;
		if (context == null || !elements.containsKey(context)){
			return;
		}
		
		final ProgressSpinnerPanel element = elements.get(context);
		
		switch(event.getTask().getStatus()){
		case Ready:
			break;  // Ignored
		case Running:
			// If a new task has begun, show progress spinner
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
			Dialogs.errorDialog(element, ((TaskExceptionEvent)event).exception);
		case Success:
		case Failure:
			element.reset();
			break;
		}
	}
}
