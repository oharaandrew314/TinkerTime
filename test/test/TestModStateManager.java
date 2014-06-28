package test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;

public class TestModStateManager {

	private static Mod MOD1, MOD2;
	private Mod mod1, mod2;
	private ModStateManager stateManager;
	private Path path;

	@BeforeClass
	public static void setUpClass() {
		MOD1 = new Mod(
				UnitTestSuite
						.getModPage("MechJeb",
								"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"));
		MOD2 = new Mod(
				UnitTestSuite
						.getModPage("Kerbal Engineer Redux",
								"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"));
	}

	private static Mod getUpdatedMod(final Mod mod, final String newestFile) {
		Mod mocked = spy(mod);
		when(mocked.isNewer(mod)).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return true;
			}
		});
		when(mocked.getNewestFile()).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return newestFile;
			}
		});

		return new Mod(mocked);
	}

	@Before
	public void setUp() {
		path = UnitTestSuite.getTempFile("mods", ".json");

		mod1 = new Mod(MOD1);
		mod2 = new Mod(MOD2);

		stateManager = new ModStateManager(path);
	}

	@After
	public void tearDown() {
		FileUtils.deleteQuietly(path.toFile());
	}

	@Test
	public void testSaveOne() {
		stateManager.modUpdated(mod1);

		assertEquals(1, stateManager.getMods().size());
		assertTrue(stateManager.getMods().contains(mod1));
	}

	@Test
	public void testSaveTwo() {
		testSaveOne();

		stateManager.modUpdated(mod2);

		assertEquals(2, stateManager.getMods().size());
		assertTrue(stateManager.getMods().contains(mod2));
	}

	@Test
	public void testSaveDuplicate() {
		testSaveOne();
		testSaveOne();
	}

	@Test
	public void testSaveUpdate() {
		testSaveOne();
		
		String newestFile = mod1.getNewestFile() + "-updated";
		Mod newer = getUpdatedMod(mod1, newestFile);
		assertEquals(newestFile, newer.getNewestFile());
		
		stateManager.modUpdated(newer);
		
		Set<Mod> mods = stateManager.getMods();
		assertEquals(1, mods.size());
		assertTrue(mods.contains(newer));
		assertEquals(newestFile, mods.iterator().next().getNewestFile());
	}

}
