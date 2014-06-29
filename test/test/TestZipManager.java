package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.junit.Before;
import org.junit.Test;

import aohara.tinkertime.controllers.files.ZipManager;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class TestZipManager {

	private static final Path ZIP = Paths.get("test/res/TestMod.zip");
	private static final ModStructure STRUCT = new ModStructure(ZIP);

	private Path gameDataPath;

	@Before
	public void setUp() {
		gameDataPath = UnitTestSuite.getTempDir("testZip");
	}

	@Test
	public void testGetEntries() {
		Set<String> actualNames = new HashSet<>();
		for (ZipEntry entry : ZipManager.getZipEntries(ZIP)) {
			if (!entry.isDirectory()){
				actualNames.add(entry.getName());
			}	
		}

		HashSet<String> expectedNames = new HashSet<>();
		expectedNames.add("readme.txt");
		expectedNames.add("TestMod1/TestMod1.txt");
		expectedNames.add("Dependency/part1.txt");
		expectedNames.add("TestMod1/Plugins/Foo.dll");
		expectedNames.add("TestMod1/Parts/Fuel/BigTank/BigTank.tank");
		expectedNames.add("TestMod1/Icons/icon.ico");
		expectedNames.add("Dependency/Dependency.txt");

		assertEquals(expectedNames, actualNames);
	}

	@Test
	public void testUnzip() {
		try {
			for (Module module : STRUCT.getModules()) {
				ZipManager.unzipModule(module, gameDataPath);
				for (Path filePath : module.getFilePaths()) {
					Path expectedPath = gameDataPath.resolve(filePath);
					assertTrue(expectedPath.toFile().exists());
				}
			}
		} catch (IOException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testGetFileText() {
		assertEquals(
			"readme for TestMod",
			ZipManager.getFileText(ZIP, "readme.txt")
		);
	}

}
