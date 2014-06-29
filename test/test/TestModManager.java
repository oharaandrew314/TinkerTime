package test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import test.util.MockConfig;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModDownloadManager;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModUpdateListener;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.controllers.files.ConflictResolver.Resolution;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModPage;

public class TestModManager {
	
	private ModDownloadManager dm;
	private ModUpdateListener ul;
	private Config config;
	
	@Before
	public void setUp(){
		dm = mock(ModDownloadManager.class);
		ul = mock(ModUpdateListener.class);
		config = new MockConfig();
	}
	
	@Test
	public void testIsDownloaded(){
		
	}

	@Test
	public void testAddMod() {
		/*
		final ModPage page = UnitTestSuite.getModPage(
			"Kerbal Engineer Redux",
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"
		);
		*/
		//Mod mod = ModManager.addNewMod(page, dm, ul);
	
		//verify(dm, times(1)).downloadMod(mod);
	}
	
	@Test
	public void testConflicts(){
		
	}
	
	public static class MockConflictResolver extends ConflictResolver {
		
		public Resolution res;

		@Override
		public Resolution getResolution(Path Conflict, Mod mod) {
			return res;
		}
	}

}
