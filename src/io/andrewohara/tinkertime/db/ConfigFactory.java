package io.andrewohara.tinkertime.db;

import io.andrewohara.tinkertime.models.ConfigData;

public interface ConfigFactory {

	public ConfigData getConfig();
	public void update(ConfigData config);
}
