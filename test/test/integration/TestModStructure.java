package test.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import test.util.TestModLoader;
import test.util.ModStubs;
import thirdParty.ZipNode;
import aohara.tinkertime.models.ModStructure;

public class TestModStructure {
	
	private void testModules(ModStubs stub, String... expectedModuleNames) throws IOException{
		ModStructure struct = TestModLoader.getStructure(stub);
		
		// Get Actual Module Names
		Set<String> actualNames = new HashSet<>();
		for (ZipNode module : struct.getModules()){
			actualNames.add(module.getName());
		}

		assertEquals(
			new HashSet<String>(Arrays.asList(expectedModuleNames)),
			actualNames
		);
	}
	
	private void testModuleFiles(ModStructure struct, String moduleName, String... expectedFileNames){
		ZipNode module = TestModLoader.getModule(struct, moduleName);	
		
		Set<String> actual = new HashSet<>();
		getFilePaths(actual, module.getParent(), module);
        assertThat(actual, containsInAnyOrder(expectedFileNames));
	}
	
	private void getFilePaths(Set<String> paths, ZipNode gameDataNode, ZipNode node){		
		for (ZipNode child : node.getChildren().values()){
			if (!child.isDirectory()){
				paths.add(child.getPathFrom(gameDataNode));
			} else {
				getFilePaths(paths, gameDataNode, child);
			}
		}
	}

	@Test
	public void testMod1() throws IOException{
		testModules(ModStubs.TestMod1, "TestMod1/", "Dependency/");
		testModuleFiles(
			TestModLoader.getStructure(ModStubs.TestMod1),
			"TestMod1/",
			"TestMod1/Plugins/Foo.dll",
			"TestMod1/Icons/icon.ico",
			"TestMod1/TestMod1.txt",
			"TestMod1/Parts/Fuel/BigTank/BigTank.tank"
		);
	}
	
	@Test
	public void testMod2() throws IOException{
		testModules(ModStubs.TestMod2, "TestMod2/", "Dependency/");
		testModuleFiles(
			TestModLoader.getStructure(ModStubs.TestMod2),
			"TestMod2/",
			"TestMod2/Plugins/Foo.dll",
			"TestMod2/Icons/icon.ico",
			"TestMod2/TestMod2.txt",
			"TestMod2/Parts/Fuel/BigTank/BigTank.tank"
		);
	}
	
	@Test
	public void testEngineerEntries() throws IOException{
		testModules(ModStubs.Engineer, "Engineer/");
	}
	
	@Test
	public void testMechjeb() throws IOException{
		testModules(ModStubs.Mechjeb, "MechJeb2/");
	}
	
	@Test
	public void testAlarmClock() throws IOException{
		testModules(ModStubs.AlarmClock, "TriggerTech/");
	}
	
	@Test
	public void testEnhancedNavball() throws IOException{
		testModules(ModStubs.NavBall, "EnhancedNavBall/");
	}	
	
	@Test
	public void testHotRockets() throws IOException{
		testModules(ModStubs.HotRockets, "SmokeScreen/", "MP_Nazari/");
	}
	
	@Test
	public void testNear() throws IOException {
		testModules(ModStubs.Near, "NEAR/");
	}
	
	@Test
	public void testRadialEngineMounts() throws IOException {
		testModules(ModStubs.RadialEngines, "RadialEngineMountsPPI/");
	}
}
