package io.andrewohara.tinkertime.db;

import io.andrewohara.tinkertime.models.Installation;

import java.util.Collection;

public interface InstallationManager {

	public Collection<Installation> getInstallations();
	public void delete(Installation installation);
	public void update(Installation installation);
	public void changeInstallation(Installation installation);

}
