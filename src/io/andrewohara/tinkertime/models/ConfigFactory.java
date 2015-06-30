package io.andrewohara.tinkertime.models;

public interface ConfigFactory {

	public ConfigData getConfig();
	public void update(ConfigData config);
}
