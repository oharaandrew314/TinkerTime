package io.andrewohara.tinkertime.controllers;

import io.andrewohara.common.workflows.tasks.TaskCallback;
import io.andrewohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.views.SelectedInstallationView;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelController;

import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ModUpdateCoordinator extends TaskCallback implements ModUpdateHandler {

	private final ConfigData config;

	private ModUpdateHandler modSelector, installationView;
	private ModListCellRenderer modListCellRenderer;

	@Inject
	protected ModUpdateCoordinator(ConfigData config){
		this.config = config;
	}

	public void setListeners(ModSelectorPanelController modSelector, ModListCellRenderer modListCellRender, SelectedInstallationView installationView){
		this.modSelector = modSelector;
		this.modListCellRenderer = modListCellRender;
		this.installationView = installationView;
	}

	@Override
	public void changeInstallation(Installation newInstallation){
		try {
			config.setSelectedInstallation(newInstallation);
			modSelector.changeInstallation(newInstallation);
			installationView.changeInstallation(newInstallation);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void processTaskEvent(TaskEvent event) {
		modListCellRenderer.handleTaskEvent(event);
	}
}
