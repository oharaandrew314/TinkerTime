package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.util.ModLoader;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.config.Config.IllegalPathException;
import aohara.tinkertime.controllers.ModDownloadManager;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyEnabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyUpToDateException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.controllers.files.ConflictResolver.Resolution;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.ModStructure.Module;

public class TestModManager {
	
	private MockDM dm;
	private ModStateManager sm;
	private Config config;
	private ModManager manager;
	private static ModPage MECHJEB;
	private Mod mod, testMod1, testMod2;
	private MockCR cr;
	
	@BeforeClass
	public static void setUpClass(){
		MECHJEB = ModLoader.getPage(ModLoader.MECHJEB);
	}
	
	@Before
	public void setUp() throws Throwable {
		manager = new ModManager(
			sm = mock(ModStateManager.class),
			dm = spy(new MockDM()),
			config = spy(new Config()),
			cr = spy(new MockCR(config, sm))
		);
		
		try {
			Path kerbalPath = UnitTestSuite.getTempDir("ksp");
			kerbalPath.resolve(Config.KSP_EXE).toFile().createNewFile();
			kerbalPath.resolve("GameData").toFile().mkdirs();
			
			config.setKerbalPath(kerbalPath);
			config.setModsPath(UnitTestSuite.getTempDir("mods"));
		} catch (IllegalPathException | IOException e) {
			e.printStackTrace();
		}
		
		mod = ModLoader.addMod(ModLoader.MECHJEB, config);
		testMod1 = ModLoader.addMod(ModLoader.TESTMOD1, config);
		testMod2 = ModLoader.addMod(ModLoader.TESTMOD2, config);
	}
	
	// -- Tests -----------------------------------------------
	
	@Test
	public void testAddMod() throws Throwable {
		Mod mod = manager.addNewMod(MECHJEB);
	
		verify(dm, times(1)).downloadMod(mod);
		verify(sm, times(1)).modUpdated(mod, false);
	}

	@Test
	public void testIsDownloaded() throws IOException {
		ModApi mod = ModLoader.getPage(ModLoader.ENGINEER);
		Path zipPath = config.getModZipPath(mod);
		
		assertFalse(manager.isDownloaded(mod));
		verify(config, times(2)).getModZipPath(mod);
		
		// create zip
		zipPath.toFile().createNewFile();
		
		assertTrue(manager.isDownloaded(mod));
		verify(config, times(3)).getModZipPath(mod);
	}
	
	// -- Enable Tests ------------------------------------
	
	private void enableMod(Mod mod) throws Throwable {
			reset(sm);
			assertTrue(manager.isDownloaded(mod));
			
			manager.enableMod(mod);
			
			verifyZeroInteractions(cr);
			verify(sm, times(1)).modUpdated(mod, false);
			assertTrue(mod.isEnabled());
		}
	
	@Test
	public void testEnableOneMod() throws Throwable {
		cr.res = ConflictResolver.Resolution.Overwrite;

		assertFalse(mod.isEnabled());
		enableMod(mod);
	}
	
	@Test(expected=ModAlreadyEnabledException.class)
	public void testEnableEnabledMod() throws Throwable {
		cr.res = ConflictResolver.Resolution.Overwrite;
		
		enableMod(mod);
		enableMod(mod);
	}
	
	// -- Enable Conflict Tests -----------------------
	
	@Test
	public void testConflictOverwrite() throws Throwable {
		cr.res = Resolution.Overwrite;
		
		manager.enableMod(testMod1);
		manager.enableMod(testMod2);
	}
	
	@Test
	public void testConflictSkip() throws Throwable {
		cr.res = Resolution.Skip;
		
		manager.enableMod(testMod1);
		manager.enableMod(testMod2);
	}
	
	// -- Disable Tests -------------------------------
	
	@Test
	public void testDisableMod() throws Throwable {
		enableMod(mod);
		manager.disableMod(mod);
		assertFalse(mod.isEnabled());
		assertTrue(manager.isDownloaded(mod));
	}
	
	@Test(expected = ModAlreadyDisabledException.class)
	public void testDisableDisabledMod() throws Throwable {
		assertFalse(mod.isEnabled());
		manager.disableMod(mod);
	}
	
	// -- Update Tests --------------------------------
	
	@Test(expected = ModAlreadyUpToDateException.class)
	public void testModAlreadyUpToDate() throws Throwable {
		dm.allowUpdate = false;
		manager.updateMod(mod);
	}
	
	@Test
	public void testUpdateDisabledMod() throws Throwable {
		assertFalse(mod.isEnabled());
		manager.updateMod(mod);
		verify(dm, times(1)).downloadMod(mod);
		assertFalse(mod.isEnabled());
	}
	
	@Test
	public void testUpdateEnabledMod() throws Throwable {
		mod.setEnabled(true);
		assertTrue(mod.isEnabled());
		manager.updateMod(mod);
		verify(dm, times(1)).downloadMod(mod);
		assertFalse(mod.isEnabled());
	}
	
	// -- Mock Objects -------------------------------------
	
	public static class MockCR extends ConflictResolver {
		
		public MockCR(Config config, ModStateManager sm) {
			super(config, sm);
		}

		public Resolution res;

		@Override
		public Resolution getResolution(Module module, Mod mod) {
			return res;
		}
	}
	
	public static class MockDM extends ModDownloadManager {
		
		public boolean allowUpdate = true;
		
		@Override
		public void tryUpdateData(Mod mod)
				throws ModAlreadyUpToDateException, ModUpdateFailedException {
			if (!allowUpdate){
				throw new ModAlreadyUpToDateException();
			}
		}
	}
}
