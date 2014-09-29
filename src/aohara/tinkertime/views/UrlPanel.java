package aohara.tinkertime.views;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import aohara.common.Util;
import aohara.common.selectorPanel.DecoratedComponent;

/**
 * Decorated JLabel which contains a clickable URL.
 * 
 * If the URL is clicked, the page will be loaded using system's default browser.
 * 
 * @author Andrew O'Hara
 */
public class UrlPanel extends MouseAdapter implements DecoratedComponent<JLabel> {
	
	private final URL url;
	private final JLabel label = new JLabel();
	
	public UrlPanel(URL url){
		this(url.toString(), url);
	}
	
	public UrlPanel(String text, URL url){
		this.url = url;
		label.setText(String.format(
			"<html><a href='%s'>%s</a></html>",
			url.toString(),
			text
		));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(this);
	}

	@Override
	public JLabel getComponent() {
		return label;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			Util.goToHyperlink(url);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(
	        	label, "Could not open hyperlink:\n" + url);
		}
	}

}
