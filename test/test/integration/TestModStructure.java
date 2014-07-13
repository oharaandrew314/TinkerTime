package test.integration;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import test.util.ModLoader;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class TestModStructure {
	
	private void testModules(String modName, String... expectedModuleNames){
		ModStructure struct = ModLoader.getStructure(modName);
		
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
		Set<String> actual = new HashSet<String>();
		for (Path path : module.getOutput().values()){
			actual.add(path.toString());
		}
		
		assertEquals(
			new HashSet<String>(Arrays.asList(expectedFileNames)),
			actual
		);
	}
	
	@Test
	public void testMod1(){
		testModules(ModLoader.TESTMOD1, "TestMod1", "Dependency");
		
		ModStructure struct = ModLoader.getStructure(ModLoader.TESTMOD1);
		for (Module module : struct.getModules()){
			if (module.getName().equals("TestMod1")){
				testModule(
					module,
					"TestMod1\\Plugins\\Foo.dll",
					"TestMod1\\Icons\\icon.ico",
					"TestMod1\\TestMod1.txt",
					"TestMod1\\Parts\\Fuel\\BigTank\\BigTank.tank"
				);
			} else if (module.getName().equals("Dependency")){
				// No Action
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	@Test
	public void testMod2(){
		testModules(ModLoader.TESTMOD2, "TestMod2", "Dependency");
		
		ModStructure struct = ModLoader.getStructure(ModLoader.TESTMOD2);
		for (Module module : struct.getModules()){
			if (module.getName().equals("TestMod2")){
				testModule(
						module,
						"TestMod2\\Plugins\\Foo.dll",
						"TestMod2\\Icons\\icon.ico",
						"TestMod2\\TestMod2.txt",
						"TestMod2\\Parts\\Fuel\\BigTank\\BigTank.tank"
					);;
				
			} else if (module.getName().equals("Dependency")){
				
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	@Test
	public void testEngineerEntries(){
		testModules(ModLoader.ENGINEER, "Engineer");
	}
	
	@Test
	public void testMechjeb(){
		testModules(ModLoader.MECHJEB, "MechJeb2");
	}
	
	@Test
	public void testAlarmClock(){
		testModules(ModLoader.ALARMCLOCK, "TriggerTech");
	}
	
	@Test
	public void testEnhancedNavball(){
		testModules(ModLoader.NAVBALL, "EnhancedNavBall");
	}	
	
	@Test
	public void testHotRockets(){
		testModules(ModLoader.HOTROCKETS, "SmokeScreen", "MP_Nazari", "ModuleManager.2.1.0.dll");
	}
}
