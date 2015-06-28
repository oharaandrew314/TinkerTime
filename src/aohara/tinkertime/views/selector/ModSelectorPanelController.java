package aohara.tinkertime.views.selector;

import java.awt.Component;
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

import javax.swing.JSplitPane;

import aohara.common.views.Dialogs;
import aohara.common.views.selectorPanel.DecoratedComponent;
import aohara.common.views.selectorPanel.SelectorPanelController;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModUpdateHandler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;

public class ModSelectorPanelController implements ModUpdateHandler, DecoratedComponent<JSplitPane> {

	private final SelectorPanelController<Mod> spc;
	
	ModSelectorPanelController(SelectorPanelController<Mod> spc, ModManager mm){
		this.spc = spc;
		new DropTarget(spc.getList(), new DragDropListener(spc.getList(), mm));
	}

	@Override
	public void modUpdated(Mod mod) {
		spc.add(mod);
	}

	@Override
	public void modDeleted(Mod mod) {
		spc.remove(mod);
	}

	@Override
	public void clear() {
		spc.clear();
	}

	@Override
	public JSplitPane getComponent() {
		return spc.getComponent();
	}
	
private static class DragDropListener implements DropTargetListener {
		
		private final Component listenTo;
		private final ModManager mm;
		
		private DragDropListener(Component listenTo, ModManager mm){
			this.listenTo = listenTo;
			this.mm = mm;
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
