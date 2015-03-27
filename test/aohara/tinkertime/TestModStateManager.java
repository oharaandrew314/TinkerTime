package aohara.tinkertime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import aohara.tinkertime.controllers.ModLoader;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.testutil.MockHelper;
import aohara.tinkertime.testutil.ModStubs;
import aohara.tinkertime.testutil.TestModLoader;

public class TestModStateManager {

	private Mod mod1, mod2;
	private ModLoader modLoader;
	private Set<Mod> mods;
	
	private static Mod getUpdatedMod(final Mod mod, final String newestFile){
		return new Mod(
			mod.id,
			mod.getName(),
			newestFile,
			mod.getCreator(),
			mod.getPageUrl(),
			mod.getUpdatedOn(),
			mod.getSupportedVersion()
		);
		
	}
	
	private void update(Mod mod, boolean deleted){
		if (deleted){
			modLoader.modDeleted(mod);
		} else {
			modLoader.modUpdated(mod);
		}
		
		mods = modLoader.getMods();
	}

	@Before
	public void setUp() throws Throwable {
		mod1 = TestModLoader.loadMod(ModStubs.Mechjeb);
		mod2 = TestModLoader.loadMod(ModStubs.Engineer);

		modLoader = ModLoader.create(MockHelper.newConfig());
	}

	@Test
	public void testSaveOne() {
		update(mod1, false);

		assertEquals(1, mods.size());
		assertTrue(mods.contains(mod1));
	}

	@Test
	public void testSaveTwo() {
		testSaveOne();

		update(mod2, false);

		assertEquals(2, mods.size());
		assertTrue(mods.contains(mod2));
	}

	@Test
	public void testSaveDuplicate() {
		testSaveOne();
		testSaveOne();
	}

	@Test
	public void testSaveUpdatedMod() throws Throwable {	
		testSaveOne();
		
		String newestFile = mod1.getNewestFileName() + "-updated";
		Mod newer = getUpdatedMod(mod1, newestFile);
		assertEquals(newestFile, newer.getNewestFileName());
		
		update(newer, false);
		
		assertEquals(1, mods.size());
		assertTrue(mods.contains(newer));
		assertEquals(newestFile, mods.iterator().next().getNewestFileName());
	}
	
	@Test
	public void testModDeleted(){
		update(mod1, false);
		update(mod2, false);
		
		assertEquals(2, mods.size());
		
		update(mod1, true);
		assertEquals(1, mods.size());
		assertFalse(mods.contains(mod1));
		assertTrue(mods.contains(mod2));
	}

}
