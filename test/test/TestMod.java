package test;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import test.util.MockConfig;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.models.Mod;

public class TestMod {
	
	private static TinkerConfig config;
	
	@BeforeClass
	public static void setUpClass(){
		config = new MockConfig();
	}
	
	private void checkZipPath(String expectedFileName, String originalFileName){
		checkZipPath(
			expectedFileName,
			new Mod(null, null, originalFileName, null, null, null, null, null)
		);
	}
	
	private void checkZipPath(String expectedFileName, Mod mod){
		assertEquals(Paths.get("zips", expectedFileName), mod.getCachedZipPath(config));
	}
	
	@Test
	public void testZipPathWithIllegalCharacters() {		
		checkZipPath("ThisThatStuff.zip", "This/That/Stuff.zip");
		checkZipPath("KSP Intergalactic Redux.zip", "KSP: Intergalactic: Redux.zip");	
	}

}
