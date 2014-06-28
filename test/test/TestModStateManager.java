package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
	private List<Mod> mods;

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
	
	private void update(Mod mod){
		stateManager.modUpdated(mod);
		mods = new ArrayList<Mod>(stateManager.getMods());
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
		update(mod1);

		assertEquals(1, mods.size());
		assertTrue(mods.contains(mod1));
	}

	@Test
	public void testSaveTwo() {
		testSaveOne();

		update(mod2);

		assertEquals(2, mods.size());
		assertTrue(mods.contains(mod2));
	}

	@Test
	public void testSaveDuplicate() {
		testSaveOne();
		testSaveOne();
	}

	@Test
	public void testSaveUpdatedMod() {
		testSaveOne();
		
		String newestFile = mod1.getNewestFile() + "-updated";
		Mod newer = getUpdatedMod(mod1, newestFile);
		assertEquals(newestFile, newer.getNewestFile());
		
		update(newer);
		
		assertEquals(1, mods.size());
		assertTrue(mods.contains(newer));
		assertEquals(newestFile, mods.get(0).getNewestFile());
	}
	
	@Test
	public void testSaveModState(){
		boolean mod1State = false;
		mod1.setEnabled(mod1State);
		update(mod1);
		
		assertEquals(mod1State, mods.get(0).isEnabled());
		
		boolean mod2State = true;
		mod2.setEnabled(mod2State);
		update(mod2);
		
		assertEquals(mod1State, mods.get(0).isEnabled());
		assertEquals(mod2State, mods.get(1).isEnabled());
	}

}
