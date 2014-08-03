package test;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.util.ModLoader;
import aohara.tinkertime.controllers.ZipManager;
import aohara.tinkertime.models.ModStructure;

public class TestZipManager {

	private static ModStructure STRUCT;
	
	private ZipManager zipManager;
	
	@BeforeClass
	public static void setUpClass(){
		STRUCT = ModLoader.getStructure(ModLoader.TESTMOD1);
	}

	@Before
	public void setUp() {
		zipManager = STRUCT.getZipManager();
	}

	@Test
	public void testGetEntries() {
		Set<String> actualNames = new HashSet<>();
		for (ZipEntry entry : zipManager.getZipEntries()) {
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
	public void testGetFileText() {
		assertEquals(
			"readme for TestMod",
			zipManager.getFileText("readme.txt")
		);
	}
}
