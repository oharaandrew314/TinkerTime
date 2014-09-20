package aohara.tinkertime.controllers.fileUpdater;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.views.FileUpdateDialog;

/**
 * Download Action used by the {@link aohara.tinkertime.views.FileUpdateDialog}.
 * 
 * This Class is to be used as an Action for initiating the download of a File
 * initiated by the {@link aohara.tinkertime.views.FileUpdateDialog}.
 * 
 * Additional interfaces can be implemented for providing feedback to the View
 * once the download is complete.
 * 
 * @author Andrew O'Hara
 */
@SuppressWarnings("serial")
public abstract class FileDownloadController extends AbstractAction {
	
	private final Crawler<?> crawler;
	private FileUpdateDialog dialog;
	
	protected FileDownloadController(Crawler<?> crawler){
		super("Update");
		this.crawler = crawler;
	}
	
	public FileUpdateDialog getDialog(){
		return dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (dialog == null){
			throw new IllegalStateException("Cannot run without a dialog to report to");
		}
		
		try {
			download(crawler);
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error Updating\n\n" + e1.toString());
		}
	}
	
	public void setFileUpdateDialog(FileUpdateDialog dialog){
		this.dialog = dialog;
	}
	
	protected abstract void download(Crawler<?> crawler) throws IOException;
	
}
