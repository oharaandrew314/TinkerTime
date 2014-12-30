package test.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import test.util.TestModLoader;
import test.util.ModStubs;
import aohara.common.tree.TreeNode;
import aohara.tinkertime.models.ModStructure;

public class TestModStructure {
	
	private void testModules(ModStubs stub, String... expectedModuleNames) throws IOException{
		ModStructure struct = TestModLoader.getStructure(stub);
		
		// Get Actual Module Names
		Set<String> actualNames = new HashSet<>();
		for (TreeNode module : struct.getModules()){
			actualNames.add(module.getName());
		}

		assertEquals(
			new HashSet<String>(Arrays.asList(expectedModuleNames)),
			actualNames
		);
	}
	
	private void testModuleFiles(ModStructure struct, String moduleName, String... expectedFileNames){
		TreeNode module = TestModLoader.getModule(struct, moduleName);
		
		Set<Path> expectedPaths = new HashSet<>();
		for (String path : expectedFileNames){
			expectedPaths.add(Paths.get(path));
		}
		
		Set<Path> actual = new HashSet<>();
		for (TreeNode node : module.getAllChildren()){
			if (!node.isDir()){
				actual.add(node.getPath());
			}
		}
		
		assertEquals(expectedPaths, actual);
	}

	@Test
	public void testMod1() throws IOException{
		testModules(ModStubs.TestMod1, "TestMod1", "Dependency");
		testModuleFiles(
			TestModLoader.getStructure(ModStubs.TestMod1),
			"TestMod1",
			"TestMod1/Plugins/Foo.dll",
			"TestMod1/Icons/icon.ico",
			"TestMod1/TestMod1.txt",
			"TestMod1/Parts/Fuel/BigTank/BigTank.tank"
		);
	}
	
	@Test
	public void testMod2() throws IOException{
		testModules(ModStubs.TestMod2, "TestMod2", "Dependency");
		testModuleFiles(
			TestModLoader.getStructure(ModStubs.TestMod2),
			"TestMod2",
			"TestMod2/Plugins/Foo.dll",
			"TestMod2/Icons/icon.ico",
			"TestMod2/TestMod2.txt",
			"TestMod2/Parts/Fuel/BigTank/BigTank.tank"
		);
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
	
	@Test
	public void testCollisionFx() throws IOException {
		testModules(ModStubs.CollisionFx, "CollisionFX");
	}
}
