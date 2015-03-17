package test.util;

import java.net.MalformedURLException;
import java.net.URL;

public enum ModStubs {
	TestMod1("TestMod1", "http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"),
	TestMod2("TestMod2", "http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"),
	Engineer("Kerbal Engineer Redux", "http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"),
	Mechjeb("MechJeb", "http://www.curse.com/ksp-mods/kerbal/220221-mechjeb"),
	AlarmClock("Kerbal Alarm Clock", "http://www.curse.com/ksp-mods/kerbal/220289-kerbal-alarm-clock"),
	NavBall("Enhanced Navball", "http://www.curse.com/ksp-mods/kerbal/220469-enhanced-navball-v1-2"),
	HotRockets("HotRockets! Particle FX Replacement", "http://www.curse.com/ksp-mods/kerbal/220207-hotrockets-particle-fx-replacement"),
	Eve("EnvironmentalVisualEnhancements", "https://github.com/rbray89/EnvironmentalVisualEnhancements"),
	KerbalAlarmClock("KerbalAlarmClock", "https://github.com/TriggerAu/KerbalAlarmClock"),
	ProceduralFairings("ProceduralFairings", "https://github.com/e-dog/ProceduralFairings"),
	Near("NEAR", "https://github.com/ferram4/Ferram-Aerospace-Research"),
	RadialEngines("Radial Engine Mounts by PanaTee Parts International", "https://kerbalstuff.com/api/mod/153"),
	TimeControl("Time Control", "https://kerbalstuff.com/api/mod/21"),
	CollisionFx("Collision FX 2.1", "https://kerbalstuff.com/mod/381"),
	StockFixes("KSP-Stock-Bug-Fix-Modules", "https://github.com/ClawKSP/KSP-Stock-Bug-Fix-Modules"),
	ActiveTextureManagement("ActiveTextureManagement", "https://github.com/rbray89/ActiveTextureManagement");
	
	public final String name;
	public final URL url;
	
	private ModStubs(String name, String url){
		this.name = name;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
