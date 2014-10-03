package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import test.util.MockConfig;
import test.util.ModLoader;
import test.util.ModStubs;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;

public class TestModStateManager {

	private Mod mod1, mod2;
	private ModStateManager stateManager;
	private List<Mod> mods;
	
	private static Mod getUpdatedMod(final Mod mod, final String newestFile){
		return new Mod(
			mod.getName(),
			newestFile,
			mod.getCreator(),
			mod.getImageUrl(),
			mod.getPageUrl(),
			mod.getUpdatedOn(),
			mod.getSupportedVersion()
		);
		
	}
	
	private void update(Mod mod, boolean deleted){
		stateManager.modUpdated(mod, deleted);
		mods = new ArrayList<Mod>(stateManager.getMods());
	}

	@Before
	public void setUp() throws Throwable {
		mod1 = ModLoader.loadMod(ModStubs.Mechjeb);
		mod2 = ModLoader.loadMod(ModStubs.Engineer);

		stateManager = new ModStateManager(new MockConfig());
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
		assertEquals(newestFile, mods.get(0).getNewestFileName());
	}
	
	@Test
	public void testSaveModState(){
		boolean mod1State = false;
		mod1.setEnabled(mod1State);
		update(mod1, false);
		
		assertEquals(mod1State, mod1.isEnabled());
		assertEquals(mod1State, mods.get(0).isEnabled());
		
		boolean mod2State = true;
		mod2.setEnabled(mod2State);
		update(mod2, false);
		
		assertEquals(mod2State, mod2.isEnabled());
		for (Mod mod : mods){
			if (mod.equals(mod1)){
				assertEquals(mod1State, mod.isEnabled());
			} else if (mod.equals(mod2)){
				assertEquals(mod2State, mod.isEnabled());
			} else {
				assertTrue(false);
			}
		}
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
