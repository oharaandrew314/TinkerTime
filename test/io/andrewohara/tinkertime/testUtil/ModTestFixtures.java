package io.andrewohara.tinkertime.testUtil;

import io.andrewohara.common.version.Version;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.models.mod.ModUpdateData;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class ModTestFixtures {

	////////////////////
	// Curse Fixtures //
	////////////////////

	public Mod getCurseEngineerMod(){
		return MockMod.newMockMod(1, "http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux", "Kerbal Engineer Redux", "cybutek", getDate(2014, 4, 12), "0.6.2.4", "0.23.5");
	}

	public Mod getKSRadialMounts(){
		return MockMod.newMockMod(2, "https://kerbalstuff.com/api/mod/153", "Radial Engine Mounts by PanaTee Parts International", "teejaye85", Calendar.getInstance().getTime(), "0.30", "0.24.2");
	}

	public Mod getCurseEnhancedNavball(){
		// Not all data included.  Update
		return MockMod.newMockMod(3, "http://www.curse.com/ksp-mods/kerbal/220469-enhanced-navball-v1-3", "Enhanced Navball", "author", null, "1.3.0", "");
	}

	public Mod getKSTweakableEverything(){
		// Not all data included.  Update
		return MockMod.newMockMod(4, "https://kerbalstuff.com/mod/255", "TweakableEverything", "author", null, "1.0", "1.0");
	}

	public Mod getCurseMechjeb(){
		return MockMod.newMockMod(5, "http://www.curse.com-ksp-mods-kerbal-220221-mechjeb",  "MechJeb", "r4m0n", getDate(2014, 4, 6), "2.2.2.1.0", "0.23.5");
	}

	public Mod getCurseHotRockets(){
		return MockMod.newMockMod(6, "http://www.curse.com/ksp-mods/kerbal/220207-hotrockets-particle-fx-replacement", "HotRockets! Particle FX Replacement", "Nazari1382", getDate(2014, 7, 1), "0.25", "0.24.2");
	}

	public Mod getKSTimeControl(){
		// No Mod zip included in test modCache
		return MockMod.newMockMod(7, "https://kerbalstuff.com/api/mod/21", "Time Control", "Xaiier", Calendar.getInstance().getTime(), "13.2", "0.24.2");
	}

	public Mod getKSBackgroundProcessing(){
		return MockMod.newMockMod(8, "https://kerbalstuff.com/mod/302", "BackgroundProcessing", "jamespicone", Calendar.getInstance().getTime(), "0.4.0.1", "0.90");
	}

	public Mod getGithubKerbalAlarmClock(){
		// No Mod zip included in test modCache
		return MockMod.newMockMod(9, "https://github.com/TriggerAu/KerbalAlarmClock", "KerbalAlarmClock", "TriggerAu", getDate(2014, 12, 19), "3.2.3.0", null);
	}

	public Mod getGithubProceduralFairings(){
		// No Mod zip included in test modCache
		return MockMod.newMockMod(10, "https://github.com/e-dog/ProceduralFairings", "ProceduralFairings", "e-dog", getDate(2014, 11, 17), "3.11", null);
	}

	public Mod getGithubStockFixes(){
		// No Mod zip included in test modCache
		return MockMod.newMockMod(11, "https://github.com/ClawKSP/KSP-Stock-Bug-Fix-Modules", "KSP-Stock-Bug-Fix-Modules", "ClawKSP", getDate(2015,0,7), "0.1.7", null);
	}

	public Mod getGithubActiveTextureManagement(){
		// No Mod zip included in test modCache
		return MockMod.newMockMod(12, "https://github.com/rbray89/ActiveTextureManagement", "ActiveTextureManagement", "rbray89", getDate(2014,11,17), "4.3", null);
	}

	public Mod getJenkinsModuleManager(){
		return MockMod.newMockMod(13, "https://ksp.sarbian.com/jenkins/job/ModuleManager", "ModuleManager", "ksp.sarbian.com", getDate(2015, 1, 23), "2.5.12", null);
	}

	public Mod getKSRoverWheelSounds(){
		return MockMod.newMockMod(14, "https://kerbalstuff.com/mod/224", "Rover Wheel Sounds", "pizzaoverhead", Calendar.getInstance().getTime(), "1.2", "1.0");
	}

	//////////////////
	// Mock Objects //
	//////////////////

	private static class MockMod extends Mod {

		private final int id;

		private MockMod(int id, String url, String name, String creator, Date updatedOn, String modVersion, String kspVersion) throws SQLException{
			super(getUrl(url), null, null);
			this.id = id;
			update(new ModUpdateData(name, creator, updatedOn, Version.valueOf(modVersion), kspVersion));
		}

		private static MockMod newMockMod(int id, String url, String name, String creator, Date updatedOn, String modVersion, String kspVersion){
			try {
				return new MockMod(id, url, name, creator, updatedOn, modVersion, kspVersion);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public int getId(){
			return id;
		}

		@Override
		public Path getZipPath(){
			String path = "zips/" + getId() + ".zip";
			URL url = getClass().getClassLoader().getResource(path);
			try {
				return Paths.get(url.toURI());
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}

		private static URL getUrl(String url){
			try {
				return new URL(url);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/////////////
	// Helpers //
	/////////////

	private static Date getDate(int year, int month, int date){
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		return c.getTime();
	}
}
