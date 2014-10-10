package test.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import test.util.ModLoader;
import test.util.ModStubs;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.Module;

public class TestModStructure {
	
	private void testModules(ModStubs stub, String... expectedModuleNames) throws IOException{
		ModStructure struct = ModLoader.getStructure(stub);
		
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());			
		}

		assertEquals(
			new HashSet<String>(Arrays.asList(expectedModuleNames)),
			actualNames
		);
	}
	
	private void testModule(Module module, String... expectedFileNames){
		List<String> actual = new ArrayList<>();
		for (Path path : module.getContent().values()){
			actual.add(path.toString());
		}

        assertThat(actual, containsInAnyOrder(expectedFileNames));
	}

	@Test
	public void testMod1() throws IOException{
		testModules(ModStubs.TestMod1, "TestMod1", "Dependency");
		
		ModStructure struct = ModLoader.getStructure(ModStubs.TestMod1);
		for (Module module : struct.getModules()){
			if (module.getName().equals("TestMod1")){
				testModule(
					module,
					Paths.get("TestMod1", "Plugins", "Foo.dll").toString(),
					Paths.get("TestMod1", "Icons", "icon.ico").toString(),
					Paths.get("TestMod1", "TestMod1.txt").toString(),
					Paths.get("TestMod1", "Parts", "Fuel", "BigTank", "BigTank.tank").toString()
				);
			} else if (module.getName().equals("Dependency")){
				// No Action
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	@Test
	public void testMod2() throws IOException{
		testModules(ModStubs.TestMod2, "TestMod2", "Dependency");
		
		ModStructure struct = ModLoader.getStructure(ModStubs.TestMod2);
		for (Module module : struct.getModules()){
			if (module.getName().equals("TestMod2")){
				testModule(
						module,
						Paths.get("TestMod2", "Plugins", "Foo.dll").toString(),
						Paths.get("TestMod2", "Icons", "icon.ico").toString(),
						Paths.get("TestMod2", "TestMod2.txt").toString(),
						Paths.get("TestMod2", "Parts", "Fuel", "BigTank", "BigTank.tank").toString()
					);
				
			} else if (module.getName().equals("Dependency")){
				
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	@Test
	public void testEngineerEntries() throws IOException{
		testModules(ModStubs.Engineer, "Engineer");
	}
	
	@Test
	public void testMechjeb() throws IOException{
		testModules(ModStubs.Mechjeb, "MechJeb2");
	}
	
	@Test
	public void testAlarmClock() throws IOException{
		testModules(ModStubs.AlarmClock, "TriggerTech");
	}
	
	@Test
	public void testEnhancedNavball() throws IOException{
		testModules(ModStubs.NavBall, "EnhancedNavBall");
	}	
	
	@Test
	public void testHotRockets() throws IOException{
		testModules(ModStubs.HotRockets, "SmokeScreen", "MP_Nazari");
	}
	
	@Test
	public void testNear() throws IOException {
		testModules(ModStubs.Near, "NEAR");
	}
	
	@Test
	public void testRadialEngineMounts() throws IOException {
		testModules(ModStubs.RadialEngines, "RadialEngineMountsPPI");
	}
}
