package aohara.tinkertime.controllers.fileUpdater;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.models.pages.FilePage;
import aohara.tinkertime.views.FileUpdateDialog;

@SuppressWarnings("serial")
public abstract class FileDownloadController extends AbstractAction implements UpdateListener {
	
	private FilePage latestPage;
	private FileUpdateDialog dialog;
	
	protected FileDownloadController(){
		super("Update");
	}
	
	@Override
	public void setUpdateAvailable(FilePage latestPage){
		this.latestPage = latestPage;			
	}
	
	public FileUpdateDialog getDialog(){
		return dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (dialog == null){
			throw new IllegalStateException("Cannot run without a dialog to report to");
		} else if (latestPage == null){
			throw new IllegalStateException("Cannot run without latest page being set");
		}
		
		try {
			download(latestPage);
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(dialog, "Error Updating\n\n" + e1.toString());
		}
	}
	
	public void setFileUpdateDialog(FileUpdateDialog dialog){
		this.dialog = dialog;
	}
	
	protected abstract void download(FilePage latestPage) throws IOException;
	
}
