package io.andrewohara.tinkertime.workflows.tasks;

import static org.junit.Assert.assertEquals;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.controllers.workflows.tasks.AnalyzeModZipTask;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.Installation.InvalidGameDataPathException;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.testUtil.ModTestFixtures;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestAnalyzeModZipTask {

	private ModTestFixtures modFixtures;
	private MockUpdateCoordinator uc;

	@Before
	public void setUp() throws URISyntaxException, InvalidGameDataPathException{
		uc = new MockUpdateCoordinator();
		modFixtures = new ModTestFixtures();
	}

	private void testFiles(Mod mod, String... paths) throws IOException{

		AnalyzeModZipTask task = new AnalyzeModZipTask(mod, uc);
		task.call(null);

		Set<String> actualPaths = uc.getPathsFor(mod);

		Set<String> expectedPaths = new LinkedHashSet<>();
		for (String path : paths){
			expectedPaths.add(Paths.get(path).toString());
		}

		if (!expectedPaths.equals(actualPaths)){
			System.out.println("\nExpecting");
			for (String path : expectedPaths){
				System.out.println(path);
			}
			System.out.println("\nGot");
			for(String path : actualPaths){
				System.out.println(path);
			}
		}

		assertEquals(expectedPaths, actualPaths);
	}

	@Test
	public void testRadialEngines() throws IOException {
		testFiles(
				modFixtures.getKSRadialMounts(),
				"RadialEngineMountsPPI/basicRadialEngineMount/model.mu",
				"RadialEngineMountsPPI/basicRadialEngineMount/part.cfg",
				"RadialEngineMountsPPI/basicRadialEngineMount/texture.mbm",
				"RadialEngineMountsPPI/doubleRadialEngineMount/model.mu",
				"RadialEngineMountsPPI/doubleRadialEngineMount/part.cfg",
				"RadialEngineMountsPPI/doubleRadialEngineMount/texture.mbm"
				);
	}

	@Test
	public void testEnhancedNavball() throws IOException {
		testFiles(
				modFixtures.getCurseEnhancedNavball(),
				"EnhancedNavBall/Plugins/EnhancedNavBall.dll",
				"EnhancedNavBall/Resources/navball24.png",
				"EnhancedNavBall/Resources/navball32.png"
				);
	}

	@Test
	public void testTweakableEverything() throws IOException {
		testFiles(
				modFixtures.getKSTweakableEverything(),
				"EVAManager.dll",
				"TweakableEverything/TweakableStaging.dll",
				"TweakableEverything/TweakableSolarPanels.dll",
				"TweakableEverything/TweakableSolarPanels.cfg",
				"ToadicusTools/ToadicusTools.dll"
				);
	}

	@Test
	public void testRoverWheelSounds() throws IOException {
		testFiles(
				modFixtures.getKSRoverWheelSounds(),
				"WheelSounds/ModuleManager_WheelSounds.cfg",
				"WheelSounds/Plugins/WheelSounds.dll",
				"WheelSounds/Sounds/RoveMaxXL3.wav",
				"WheelSounds/Sounds/RoveMaxS2.wav",
				"WheelSounds/Sounds/RoveMaxS1.wav",
				"WheelSounds/Sounds/KerbalMotionTR-2L.wav"
				);
	}

	@Test
	public void testModuleManager() throws IOException {
		testFiles(
				modFixtures.getJenkinsModuleManager(),
				"ModuleManager.2.6.6.dll"
				);
	}

	//////////////////
	// Mock Objects //
	//////////////////

	private static class MockUpdateCoordinator implements ModUpdateCoordinator {

		private final Collection<ModFile> modFiles = new LinkedList<>();

		@Override
		public void updateModFiles(Mod mod, Collection<ModFile> modFiles, String readmeText) {
			this.modFiles.addAll(modFiles);
		}

		public Set<String> getPathsFor(Mod mod){
			Set<String> paths = new LinkedHashSet<>();
			for (ModFile modFile : modFiles){
				paths.add(modFile.getRelDestPath().toString());
			}
			return paths;
		}

		@Override public void updateMod(Mod mod) { }
		@Override public void setListeners(ModSelectorPanelFactory modSelectorPanelFactory, ModListCellRenderer modListCellRender) { }
		@Override public void deleteMod(Mod mod) { }
		@Override public void changeInstallation(Installation newInstallation) { }
		@Override public void updateModImage(Mod mod, BufferedImage image) { }
	}
}
