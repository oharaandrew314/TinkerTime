package test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import test.util.ModLoader;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class TestModStructure {
	
	private ModStructure struct1, struct2;
	
	@Before
	public void setUp(){
		struct1 = spy(ModLoader.getStructure(ModLoader.TESTMOD1));
		struct2 = spy(ModLoader.getStructure(ModLoader.TESTMOD2));
	}
	
	private void testModuleNames(ModStructure struct, Set<String> expectedNames){
		Set<String> actualNames = new HashSet<>();
		for (Module module : struct.getModules()){
			actualNames.add(module.getName());
		}
		
		assertEquals(expectedNames, actualNames);
	}

	@Test
	public void testModuleNamesStruct1() {		
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("TestMod1");
		expectedNames.add("Dependency");
		
		testModuleNames(struct1, expectedNames);
	}
	
	@Test
	public void testModuleNamesStruct2(){
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add("TestMod2");
		expectedNames.add("Dependency");
		
		testModuleNames(struct2, expectedNames);
	}
	
	@Test
	public void testUsesModule(){
		Module
			commonModule = ModLoader.getModule(struct1, "Dependency"),
			module1 = ModLoader.getModule(struct1, "TestMod1"),
			module2 = ModLoader.getModule(struct2, "TestMod2");
		
		assertTrue(struct1.usesModule(commonModule));
		assertTrue(struct2.usesModule(commonModule));
		
		assertTrue(struct1.usesModule(module1));
		assertFalse(struct2.usesModule(module1));
		
		assertFalse(struct1.usesModule(module2));
		assertTrue(struct2.usesModule(module2));
	}
	
	@Test
	public void testModulePaths(){
		Module commonModule = ModLoader.getModule(struct1, "Dependency");
		
		Set<Path> paths = new HashSet<Path>();
		paths.add(Paths.get("Dependency/Dependency.txt"));
		paths.add(Paths.get("Dependency/part1.txt"));
		
		assertEquals(
			paths,
			new HashSet<Path>(commonModule.getOutput().values())
		);
	}
}
