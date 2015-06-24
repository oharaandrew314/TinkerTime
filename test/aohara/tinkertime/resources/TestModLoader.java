package aohara.tinkertime.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import aohara.tinkertime.models.Mod;
import aohara.tinkertime.modules.TestModule;
import aohara.tinkertime.testutil.ModStubs;
import aohara.tinkertime.testutil.ResourceLoader;

public class TestModLoader {
	
	private Mod mod1, mod2;
	private ModMetaLoader modLoader;
	private Set<Mod> mods;
	
	private static Mod getUpdatedMod(final Mod mod, final String newestFile){
		return new Mod(
			mod.id,
			mod.name,
			newestFile,
			mod.creator,
			mod.pageUrl,
			mod.updatedOn,
			mod.getSupportedVersion(),
			null
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
		mod1 = ResourceLoader.loadMod(ModStubs.Mechjeb);
		mod2 = ResourceLoader.loadMod(ModStubs.Engineer);
		
		Injector injector = Guice.createInjector(new TestModule());
		modLoader = injector.getInstance(ModMetaLoader.class);
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
		
		String newestFile = mod1.newestFileName + "-updated";
		Mod newer = getUpdatedMod(mod1, newestFile);
		assertEquals(newestFile, newer.newestFileName);
		
		update(newer, false);
		
		assertEquals(1, mods.size());
		assertTrue(mods.contains(newer));
		assertEquals(newestFile, mods.iterator().next().newestFileName);
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
