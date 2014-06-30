package aohara.tinkertime.views;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aoahara.common.VerticalLayout;
import aoahara.common.selectorPanel.SelectorView;
import aohara.tinkertime.models.Mod;

@SuppressWarnings("serial")
public class ModView extends SelectorView<Mod> {
	
	private Mod mod;
	
	public ModView(){
		setLayout(new VerticalLayout(0, VerticalLayout.CENTER));
	}

	@Override
	public void display(Mod mod) {
		this.mod = mod;
		removeAll();
		
		if (mod != null){
			addRow("Name", mod.getName());
			addRow("Author", mod.getCreator());
			addRow("Last Updated", mod.getUpdatedOn().toString());
			addRow("Mod URL", mod.getPageUrl().getPath());
		}
	}
	
	private void addRow(String name, String value){
		add(new Row(name + ":", value));
	}
	
	private class Row extends JPanel {
		
		public Row(String name, String value){
			setLayout(new FlowLayout(FlowLayout.CENTER));
			add(new JLabel(name));
			
			JTextField textField = new JTextField(value);
			textField.setEditable(false);
			add(textField);
		}
	}

	@Override
	public Mod getElement() {
		return mod;
	}

}
