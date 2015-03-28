package aohara.tinkertime;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import aohara.common.tree.TreeNode;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.testutil.ModStubs;
import aohara.tinkertime.testutil.ResourceLoader;

public class TestModStructure {
	
	private void testModules(ModStubs stub, String... expectedModuleNames) throws IOException{
		ModStructure struct = ResourceLoader.getStructure(stub);
		
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
	public void testFar() throws IOException {
		testModules(ModStubs.FAR, "FerramAerospaceResearch");
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
