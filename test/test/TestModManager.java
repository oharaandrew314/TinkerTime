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
import aohara.tinkertime.controllers.ModManager.CannotEnableModException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyEnabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyUpToDateException;
import aohara.tinkertime.controllers.ModManager.ModNotDownloadedException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModPage;

public class TestModManager {
	
	private MockDM dm;
	private ModStateManager sm;
	private Config config;
	private ModManager manager;
	private static ModPage MECHJEB;
	private Mod mod;
	private MockCR cr;
	
	@BeforeClass
	public static void setUpClass(){
		MECHJEB = ModLoader.getPage(ModLoader.MECHJEB);
	}
	
	@Before
	public void setUp(){
		manager = new ModManager(
			sm = mock(ModStateManager.class),
			dm = spy(new MockDM()),
			config = spy(new Config())
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
		cr = spy(new MockCR());
	}
	
	// -- Tests -----------------------------------------------
	
	@Test
	public void testAddMod() {
		Mod mod = manager.addNewMod(MECHJEB);
	
		verify(dm, times(1)).downloadMod(mod);
		verify(sm, times(1)).modUpdated(mod);
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
	
	private void enableMod(Mod mod)
			throws ModAlreadyEnabledException, ModNotDownloadedException,
			CannotEnableModException
		{
			reset(sm);
			assertTrue(manager.isDownloaded(mod));
			
			manager.enableMod(mod, cr);
			
			verifyZeroInteractions(cr);
			verify(sm, times(1)).modUpdated(mod);
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
		
		public Resolution res;

		@Override
		public Resolution getResolution(Path Conflict, Mod mod) {
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
