package test;


import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import aohara.tinkertime.controllers.files.ZipManager;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class TestModStructure {
	
	private static final Path
		ZIP1 = Paths.get("test/res/TestMod.zip"),
		ZIP2 = Paths.get("test/res/TestMod2.zip");
	
	private ModStructure struct1, struct2;
	
	@Before
	public void setUp(){
		struct1 = spyStructure(ZIP1);
		struct2 = spyStructure(ZIP2);
	}
	
	private ModStructure spyStructure(Path zipPath){
		ModStructure struct = spy(new ModStructure(zipPath));
		when(struct.getZipManager()).then(new Answer<ZipManager>(){
			@Override
			public ZipManager answer(InvocationOnMock invocation)
					throws Throwable {
				return mock(ZipManager.class);
			}
		});
		return struct;
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
	
	private Module getModule(ModStructure struct, String moduleName){
		for (Module module : struct.getModules()){
			if (module.getName().equals(moduleName)){
				return module;
			}
		}
		return null;
	}
	
	@Test
	public void testUsesModule(){
		Module
			commonModule = getModule(struct1, "Dependency"),
			module1 = getModule(struct1, "TestMod1"),
			module2 = getModule(struct2, "TestMod2");
		
		assertTrue(struct1.usesModule(commonModule));
		assertTrue(struct2.usesModule(commonModule));
		
		assertTrue(struct1.usesModule(module1));
		assertFalse(struct2.usesModule(module1));
		
		assertFalse(struct1.usesModule(module2));
		assertTrue(struct2.usesModule(module2));
	}
	
	@Test
	public void testModulePaths(){
		Module commonModule = getModule(struct1, "Dependency");
		
		Set<Path> paths = new HashSet<Path>();
		paths.add(Paths.get("Dependency/Dependency.txt"));
		paths.add(Paths.get("Dependency/part1.txt"));
		
		assertEquals(paths, commonModule.getFilePaths());
	}

}
