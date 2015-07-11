package io.andrewohara.tinkertime.views;

import io.andrewohara.common.views.DecoratedComponent;
import io.andrewohara.tinkertime.controllers.ModUpdateHandler;
import io.andrewohara.tinkertime.models.Installation;

import javax.swing.JLabel;

import com.google.inject.Singleton;

@Singleton
public class SelectedInstallationView implements DecoratedComponent<JLabel>, ModUpdateHandler{

	private final JLabel label = new JLabel();

	public SelectedInstallationView() {
		changeInstallation(null);
	}

	@Override
	public JLabel getComponent() {
		return label;
	}

	@Override
	public void changeInstallation(Installation installation) {
		if (installation == null){
			label.setText("Error: No installation selected");
		} else {
			label.setText("Installation: " + installation.getName());
		}
	}
}
