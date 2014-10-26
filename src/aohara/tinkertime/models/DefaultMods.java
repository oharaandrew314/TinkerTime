package aohara.tinkertime.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

public class DefaultMods {
	
	public static Collection<Mod> getDefaults() {
		Collection<Mod> defaults = new LinkedList<>();
		
		// Add Module Manager to Defaults
		 try {
			 defaults.add(new Mod(
				"ksp.sarbian.com", "Module Manager", null, null, null,
				new URL("https://ksp.sarbian.com/jenkins/job/ModuleManager/lastSuccessfulBuild/api/json"),
				null, null
			));
		 } catch (MalformedURLException e){
			 throw new RuntimeException(e);
		 }
		
		return defaults;
	}

	public static boolean isBuiltIn(Mod mod){
		for (Mod builtIn : getDefaults()){
			if (builtIn.id.equals(mod.id)){
				return true;
			}
		}
		return false;
	}
}
