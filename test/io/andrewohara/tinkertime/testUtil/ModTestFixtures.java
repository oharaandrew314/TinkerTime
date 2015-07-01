package io.andrewohara.tinkertime.testUtil;

import io.andrewohara.common.version.Version;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.Installation.InvalidGameDataPathException;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

public class ModTestFixtures {

	private final Installation testInstallation;

	public ModTestFixtures(Installation testInstallation){
		this.testInstallation = testInstallation;
	}

	/////////////
	// Factory //
	/////////////

	public static ModTestFixtures create(){
		try {
			Path gameDataPath = Paths.get(Installation.class.getClassLoader().getResource("GameData").toURI());
			Installation installation = new Installation("test", gameDataPath);
			return new ModTestFixtures(installation);
		} catch (URISyntaxException | InvalidGameDataPathException e) {
			throw new RuntimeException(e);
		}
	}

	////////////////////
	// Curse Fixtures //
	////////////////////

	public Mod getCurseEngineerMod(){
		return new MockMod(1, "http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux", "Kerbal Engineer Redux", "cybutek", getDate(2014, 4, 12), "0.6.2.4", "0.24.2", testInstallation);
	}

	public Mod getKSRadialMounts(){
		return new MockMod(2, "https://kerbalstuff.com/api/mod/153", "Radial Engine Mounts by PanaTee Parts International", "teejaye85", Calendar.getInstance().getTime(), "0.30", "0.30", testInstallation);
	}

	public Mod getCurseEnhancedNavball(){
		// Not all data included.  Update
		return new MockMod(3, "http://www.curse.com/ksp-mods/kerbal/220469-enhanced-navball-v1-3", "Enhanced Navball", "author", null, "1.3.0", "", testInstallation);
	}

	public Mod getKSTweakableEverything(){
		// Not all data included.  Update
		return new MockMod(4, "https://kerbalstuff.com/mod/255", "TweakableEverything", "author", null, "1.0", "1.0", testInstallation);
	}

	public Mod getCurseMechjeb(){
		return new MockMod(5, "http://www.curse.com-ksp-mods-kerbal-220221-mechjeb",  "MechJeb", "r4m0n", getDate(2014, 4, 6), "2.2.2.1.0", "0.24.2", testInstallation);
	}

	public Mod getCurseHotRockets(){
		return new MockMod(6, "http://www.curse.com/ksp-mods/kerbal/220207-hotrockets-particle-fx-replacement", "HotRockets! Particle FX Replacement", "Nazari1382", getDate(2014, 7, 1), "0.25", "0.24.2", testInstallation);
	}

	public Mod getKSTimeControl(){
		// No Mod zip included in test modCache
		return new MockMod(7, "https://kerbalstuff.com/api/mod/21", "Time Control", "Xaiier", Calendar.getInstance().getTime(), "13.2", "0.24.2", testInstallation);
	}

	public Mod getKSBackgroundProcessing(){
		return new MockMod(8, "https://kerbalstuff.com/mod/302", "BackgroundProcessing", "jamespicone", Calendar.getInstance().getTime(), "0.4.0.1", "0.9.0", testInstallation);
	}

	public Mod getGithubKerbalAlarmClock(){
		// No Mod zip included in test modCache
		return new MockMod(9, "https://github.com/TriggerAu/KerbalAlarmClock", "KerbalAlarmClock", "TriggerAu", getDate(2014, 12, 19), "3.2.3.0", null, testInstallation);
	}

	public Mod getGithubProceduralFairings(){
		// No Mod zip included in test modCache
		return new MockMod(10, "https://github.com/e-dog/ProceduralFairings", "ProceduralFairings", "e-dog", getDate(2014, 11, 17), "3.11", null, testInstallation);
	}

	public Mod getGithubStockFixes(){
		// No Mod zip included in test modCache
		return new MockMod(11, "https://github.com/ClawKSP/KSP-Stock-Bug-Fix-Modules", "KSP-Stock-Bug-Fix-Modules", "ClawKSP", getDate(2015,0,7), "0.1.7", null, testInstallation);
	}

	public Mod getGithubActiveTextureManagement(){
		// No Mod zip included in test modCache
		return new MockMod(12, "https://github.com/rbray89/ActiveTextureManagement", "ActiveTextureManagement", "rbray89", getDate(2014,11,17), "4.3", null, testInstallation);
	}

	public Mod getJenkinsModuleManager(){
		return new MockMod(13, "https://ksp.sarbian.com/jenkins/job/ModuleManager", "ModuleManager", "ksp.sarbian.com", getDate(2015, 1, 23), "2.5.12", null, testInstallation);
	}

	//////////////////
	// Mock Objects //
	//////////////////

	private static class MockMod extends Mod {

		private final int id;

		private MockMod(int id, String url, String name, String creator, Date updatedOn, String modVersion, String kspVersion, Installation installation){
			super(getUrl(url), installation);
			this.id = id;
			update(name, creator, updatedOn, Version.valueOf(modVersion), kspVersion);
		}

		@Override
		public int getId(){
			return id;
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
