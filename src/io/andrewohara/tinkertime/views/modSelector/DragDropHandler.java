package io.andrewohara.tinkertime.views.modSelector;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collection;

import io.andrewohara.common.views.Dialogs;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.ModManager.ModUpdateFailedException;

class DragDropHandler implements DropTargetListener {

	private final Component listenTo;
	private final ModManager mm;
	private Dialogs dialogs;

	DragDropHandler(Component listenTo, ModManager mm, Dialogs dialogs){
		this.listenTo = listenTo;
		this.mm = mm;
		this.dialogs = dialogs;
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
			mm.downloadNewMod(new URL(url));
		} catch (IOException | SQLException | ModUpdateFailedException e) {
			dialogs.errorDialog(listenTo, e);
		}

	}

	private void handleZipFile(File file) throws SQLException{
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
		} catch (UnsupportedFlavorException | IOException | SQLException e) {
			dialogs.errorDialog(listenTo, e);
		}
	}

}
