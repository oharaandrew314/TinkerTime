package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import aohara.tinkertime.controllers.ZipManager;

public class TestZipManager {
	
	private static final Path ZIP = Paths.get("test/res/TestMod.zip");
	private Path path;
	
	@Before
	public void setUp(){
		path = UnitTestSuite.getTempDir("testZip");
	}
	
	@After
	public void after(){
		try {
			FileUtils.deleteDirectory(path.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetFiles() {	
		HashSet<String> files = new HashSet<>();		
		files.add("Dependency/part1.txt");
		files.add("TestMod1/Plugins/Foo.dll");
		files.add("Dependency/Dependency.txt");
		files.add("TestMod1/Parts/Fuel/BigTank/BigTank.tank");
		files.add("TestMod1/Icons/icon.ico");
		files.add("TestMod1/TestMod1.txt");

		assertEquals(files, ZipManager.getFiles(ZIP));
	}
	
	@Test
	public void testUnzip(){
		ZipManager.unzipFile(ZIP, path);
		for (String file : ZipManager.getFiles(ZIP)){
			assertTrue(path.resolve(file).toFile().exists());
		}
	}
	
	@Test
	public void testDeleteZipFiles(){
		testUnzip();
		ZipManager.deleteZipFiles(ZIP, path);
		
		for (String file : ZipManager.getFiles(ZIP)){
			assertFalse(path.resolve(file).toFile().exists());
		}
	}
	
	

}
