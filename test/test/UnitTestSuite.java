package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestModPage.class,
   TestZipManager.class,
   TestModManager.class,
   TestModStateManager.class,
   TestModStructure.class,
})

public class UnitTestSuite {
	
	public static Path getTempDir(String name) {
		try {
			Path path =  Files.createTempDirectory(name);
			path.toFile().deleteOnExit();
			return path;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public static Path getTempFile(String name, String suffix) {
		try {
			Path file =  Files.createTempFile(name, suffix);
			file.toFile().deleteOnExit();
			return file;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}	
}