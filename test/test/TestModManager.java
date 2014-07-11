package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.util.ModLoader;
import aohara.common.executors.Downloader;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModEnabler;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyEnabledException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.controllers.files.ConflictResolver.Resolution;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModStructure.Module;
import aohara.tinkertime.models.context.NewModPageContext;
import aohara.tinkertime.models.context.PageDownloadContext;

public class TestModManager {
	
	private ModStateManager sm;
	private static Config config;
	private ModManager manager;
	private static Mod mod, testMod1, testMod2;
	private MockCR cr;
	private Downloader pageDownloader, modDownloader;
	private ModEnabler enabler;
	
	@BeforeClass
	public static void setUpClass() throws Throwable{
		config = spy(new MockConfig());
		mod = ModLoader.addMod(ModLoader.MECHJEB, config);
		testMod1 = ModLoader.addMod(ModLoader.TESTMOD1, config);
		testMod2 = ModLoader.addMod(ModLoader.TESTMOD2, config);
	}
	
	@Before
	public void setUp() throws Throwable {		
		manager = new ModManager(
			sm = mock(ModStateManager.class),
			config,
			pageDownloader = mock(Downloader.class),
			modDownloader = mock(Downloader.class),
			enabler = mock(ModEnabler.class)
		);
		cr = spy(new MockCR(config, sm));
		
		mod.setEnabled(false);
		testMod1.setEnabled(false);
		testMod2.setEnabled(false);	
	}
	
	// -- Tests -----------------------------------------------
	
	@Test
	public void testAddMod() throws Throwable {
		manager.addNewMod(mod.getPageUrl().toString());
	
		verify(modDownloader, times(1)).submit(any(NewModPageContext.class));
	}

	@Test
	public void testIsDownloaded() throws IOException {
		ModApi mod = ModLoader.getPage(ModLoader.ENGINEER);		
		assertTrue(manager.isDownloaded(mod));
	}
	
	// -- Enable Tests ------------------------------------
	
	private void enableMod(Mod mod) throws Throwable {
			reset(enabler);
			assertTrue(ModManager.isDownloaded(mod, config));
			
			manager.enableMod(mod);
			
			verifyZeroInteractions(cr);
			verify(enabler, times(1)).enable(mod, config);
		}
	
	@Test
	public void testEnableOneMod() throws Throwable {
		cr.res = Resolution.Overwrite;

		assertFalse(mod.isEnabled());
		enableMod(mod);
	}
	
	@Test(expected=ModAlreadyEnabledException.class)
	public void testEnableEnabledMod() throws Throwable {
		cr.res = ConflictResolver.Resolution.Overwrite;
		
		mod.setEnabled(true);
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
		
		manager.enableMod(testMod1);;
		manager.enableMod(testMod2);
	}
	
	// -- Disable Tests -------------------------------
	
	@Test
	public void testDisableMod() throws Throwable {
		mod.setEnabled(true);
		
		manager.disableMod(mod);
		
		verify(enabler, times(1)).disable(mod, config);
	}
	
	@Test(expected = ModAlreadyDisabledException.class)
	public void testDisableDisabledMod() throws Throwable {
		assertFalse(mod.isEnabled());
		manager.disableMod(mod);
	}
	
	
	// -- Update Tests ---------------------------------------------------
	
	@Test
	public void testUpdate() throws ModUpdateFailedException{
		manager.updateMod(mod);
		verify(pageDownloader, times(1)).submit(any(PageDownloadContext.class));
	}
	
	// -- Mock Objects -------------------------------------
	
	private static class MockCR extends ConflictResolver {
		
		public MockCR(Config config, ModStateManager sm) {
			super(config, sm);
		}

		public Resolution res;

		@Override
		public Resolution getResolution(Module module, Mod mod) {
			return res;
		}
	}
	
	private static class MockConfig extends Config {
		
		@Override
		public Path getGameDataPath(){
			return Paths.get("/");
		}
		
		@Override
		public Path getModsPath(){
			return Paths.get("/");
		}
	}
}
