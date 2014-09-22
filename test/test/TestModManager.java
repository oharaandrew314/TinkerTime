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

import java.nio.file.Path;
import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.util.MockConfig;
import test.util.ModLoader;
import test.util.ModStubs;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.ConflictResolver.Resolution;
import aohara.common.workflows.ProgressPanel;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyEnabledException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.workflows.DisableModWorkflow;
import aohara.tinkertime.workflows.EnableModWorkflow;
import aohara.tinkertime.workflows.UpdateModWorkflow;

public class TestModManager {
	
	private static Config config;
	private ModManager manager;
	private static Mod mod, testMod1, testMod2;
	private MockCR cr;
	private Executor downloedExecutor, enablerExecutor;
	
	@BeforeClass
	public static void setUpClass() throws Throwable{
		config = spy(new MockConfig());
		mod = ModLoader.addMod(ModStubs.Mechjeb, config);
		testMod1 = ModLoader.addMod(ModStubs.TestMod1, config);
		testMod2 = ModLoader.addMod(ModStubs.TestMod2, config);
	}
	
	@Before
	public void setUp() throws Throwable {
		manager = new ModManager(
			mock(ModStateManager.class),
			config,
			mock(ProgressPanel.class),
			cr = spy(new MockCR()),
			downloedExecutor = mock(Executor.class),
			enablerExecutor = mock(Executor.class)
		);
		
		mod.setEnabled(false);
		testMod1.setEnabled(false);
		testMod2.setEnabled(false);	
	}
	
	// -- Tests -----------------------------------------------
	
	@Test
	public void testAddMod() throws Throwable {
		manager.addNewMod(mod.getPageUrl().toString());
		verify(downloedExecutor, times(1)).execute(any(UpdateModWorkflow.class));
	}
	
	// -- Enable Tests ------------------------------------
	
	private void enableMod(Mod mod) throws Throwable {
			reset(downloedExecutor);
			assertTrue(ModManager.isDownloaded(mod, config));
			
			manager.enableMod(mod);
			
			verifyZeroInteractions(cr);
			verify(enablerExecutor, times(1)).execute(any(EnableModWorkflow.class));
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
		verify(enablerExecutor, times(1)).execute(any(DisableModWorkflow.class));
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
		verify(downloedExecutor, times(1)).execute(any(UpdateModWorkflow.class));
	}
	
	// -- Mock Objects -------------------------------------
	
	private static class MockCR extends ConflictResolver {
		
		public Resolution res;

		@Override
		public Resolution getResolution(Path conflictPath) {
			return res;
		}
	}
}
