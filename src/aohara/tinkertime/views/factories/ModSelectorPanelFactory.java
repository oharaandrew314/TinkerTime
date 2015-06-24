package aohara.tinkertime.views.factories;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;

import com.google.inject.Inject;

import aohara.common.views.Dialogs;
import aohara.common.views.selectorPanel.SelectorPanelBuilder;
import aohara.common.views.selectorPanel.SelectorPanelController;
import aohara.tinkertime.controllers.ModListListener;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.menus.MenuFactory;

public class ModSelectorPanelFactory {
	
	private final ModListCellRenderer renderer;
	private final MenuFactory menuFactory;
	private final ModListListener listListener;
	private final ModView modView;
	private final ModManager mm;
	
	@Inject
	ModSelectorPanelFactory(ModListCellRenderer renderer, MenuFactory menuFactory, ModListListener listListener, ModView modView, ModManager mm){
		this.renderer = renderer;
		this.menuFactory = menuFactory;
		this.listListener = listListener;
		this.modView = modView;
		this.mm = mm;
	}
	
	public SelectorPanelController<Mod> create(Dimension panelSize, double dividerRatio){
		SelectorPanelBuilder<Mod> spBuilder = new SelectorPanelBuilder<>(panelSize, dividerRatio);
		spBuilder.setListCellRenderer(renderer);
		spBuilder.setContextMenu(menuFactory.createPopupMenu());
		spBuilder.addKeyListener(listListener);
		spBuilder.addSelectionListener(listListener);
		SelectorPanelController<Mod> sp = spBuilder.createSelectorPanel(modView);
		new DropTarget(sp.getList(), new DragDropListener(sp.getList()));
		return sp;
	}
	
	private class DragDropListener implements DropTargetListener {
		
		private final Component listenTo;
		
		private DragDropListener(Component listenTo){
			this.listenTo = listenTo;
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			try {
				File file = getFile(dtde.getTransferable());
				 if (isZip(file) || isUrl(file)){
					 dtde.acceptDrag(DnDConstants.ACTION_LINK);
				} else {
					dtde.rejectDrag();
				}
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
				dtde.rejectDrag();
			}
			listenTo.repaint();
		}
		
		private File getFile(Transferable t) throws UnsupportedFlavorException, IOException{
			Object td = t.getTransferData(DataFlavor.javaFileListFlavor);
			if (td instanceof Collection){
				for (Object value : (Collection<?>) td){
					if (value instanceof File){
						return (File) value;
					}
				}
			}
			return null;
		}
		
		private boolean isZip(File file){
			return file != null && file.getName().endsWith(".zip");
		}
		
		private boolean isUrl(File file){
			return file != null && file.getName().endsWith(".url");
		}
		
		private void handleUrlFile(File file){
				try {
					String contents = new String(Files.readAllBytes(file.toPath()));
					String url = contents.split("URL=")[1].split("]")[0];
					mm.downloadMod(new URL(url));
				} catch (IOException | UnsupportedHostException e) {
					Dialogs.errorDialog(listenTo, e);
				}
				
		}
		
		private void handleZipFile(File file){
			mm.addModZip(file.toPath());
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			// No Action
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
			// No Action
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			// No Action
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			try {
				File file = getFile(dtde.getTransferable());
				if (isZip(file)){
					handleZipFile(file);
				} else if (isUrl(file)){
					handleUrlFile(file);
				}
			} catch (UnsupportedFlavorException | IOException e) {
				Dialogs.errorDialog(listenTo, e);
			}
		}
	}
}
