package test.integration;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import test.util.ModLoader;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class TestModStructure {
	
	@Test
	public void testMod1(){
		ModStructure struct = ModLoader.getStructure(ModLoader.TESTMOD1);
		
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("TestMod1");
		expectedNames.add("Dependency");
		
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());			
		}
		
		assertEquals(expectedNames, actualNames);
	}
	
	@Test
	public void testMod2(){
		ModStructure struct = ModLoader.getStructure(ModLoader.TESTMOD2);
		
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("TestMod2");
		expectedNames.add("Dependency");
		
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());			
		}
		
		assertEquals(expectedNames, actualNames);
	}
	
	@Test
	public void testEngineerEntries(){
		ModStructure struct = ModLoader.getStructure(ModLoader.ENGINEER);
		
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("Engineer");
		
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());
		}
		
		assertEquals(expectedNames, actualNames);
	}
	
	@Test
	public void testMechjeb(){
		ModStructure struct = ModLoader.getStructure(ModLoader.MECHJEB);
		
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("MechJeb2");
		
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());			
		}
		
		assertEquals(expectedNames, actualNames);
	}
	
	@Test
	public void testAlarmClock(){
		ModStructure struct = ModLoader.getStructure(ModLoader.ALARMCLOCK);
		
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("TriggerTech");
		
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());			
		}
		
		assertEquals(expectedNames, actualNames);
	}
	
	@Test
	public void testEnhancedNavball(){
		ModStructure struct = ModLoader.getStructure(ModLoader.NAVBALL);
		
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("EnhancedNavBall");
		
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());			
		}
		
		assertEquals(expectedNames, actualNames);
	}	
}
