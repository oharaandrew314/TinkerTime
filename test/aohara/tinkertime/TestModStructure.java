package aohara.tinkertime;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import aohara.tinkertime.resources.ModStructure;
import aohara.tinkertime.testutil.ModStubs;
import aohara.tinkertime.testutil.ResourceLoader;

public class TestModStructure {
	
	private void testFiles(ModStubs stub, String... paths) throws IOException{
		ModStructure struct = ResourceLoader.getStructure(stub);
		
		Set<Path> expectedPaths = new LinkedHashSet<>();
		for (String path : paths){
			expectedPaths.add(Paths.get(path));
		}
		
		Set<Path> actualPaths = struct.getPaths();
		
		/*
		System.out.println("\nExpecting");
		for (Path path : expectedPaths){
			System.out.println(path);
		}
		System.out.println("\nGot");
		for(Path path : actualPaths){
			System.out.println(path);
		}
		*/
		
		assertEquals(expectedPaths, actualPaths);
	}
	
	@Test
	public void testRadialEngines() throws IOException {
		testFiles(
			ModStubs.RadialEngines,
			"RadialEngineMountsPPI",
			"RadialEngineMountsPPI/basicRadialEngineMount",
			"RadialEngineMountsPPI/basicRadialEngineMount/model.mu",
			"RadialEngineMountsPPI/basicRadialEngineMount/part.cfg",
			"RadialEngineMountsPPI/basicRadialEngineMount/texture.mbm",
			"RadialEngineMountsPPI/doubleRadialEngineMount",
			"RadialEngineMountsPPI/doubleRadialEngineMount/model.mu",
			"RadialEngineMountsPPI/doubleRadialEngineMount/part.cfg",
			"RadialEngineMountsPPI/doubleRadialEngineMount/texture.mbm"
		);		
	}
	
	@Test
	public void testEnhancedNavball() throws IOException {
		testFiles(
			ModStubs.NavBall,
			"EnhancedNavBall",
			"EnhancedNavBall/Plugins",
			"EnhancedNavBall/Plugins/EnhancedNavBall.dll",
			"EnhancedNavBall/Resources",
			"EnhancedNavBall/Resources/navball24.png",
			"EnhancedNavBall/Resources/navball32.png"
		);
		
	}
}
