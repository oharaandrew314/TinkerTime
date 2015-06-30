CREATE TABLE  `config` (
	`id` INTEGER PRIMARY KEY NOT NULL,
	`checkForAppUpdatesOnStartup` BOOLEAN DEFAULT 1,
	`checkForModUpdatesOnStartup` BOOLEAN DEFAULT 1,
	`numConcurrentDownloads` INTEGER DEFAULT 4,
	`selectedInstallation_id` INTEGER,
	`launchArguments` VARCHAR
);

CREATE TABLE `installations` (
	`id` INTEGER PRIMARY KEY AUTOINCREMENT,
	`name` VARCHAR NOT NULL,
	`path` VARCHAR NOT NULL
);

CREATE TABLE `mods` (
	`id` INTEGER PRIMARY KEY AUTOINCREMENT,
	`updatedOn` TIMESTAMP,
	`name` VARCHAR,
	`creator` VARCHAR,
	`modVersion` VARCHAR,
	`kspVersion` VARCHAR,
	`newestFileName` VARCHAR,
	`url` VARCHAR,
	`updateAvailable` BOOLEAN,
	`installation_id` INTEGER REFERENCES installations(id)
); 