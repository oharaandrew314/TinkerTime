package test;

import org.junit.Test;

import test.util.ModLoader;

public class TestGithubModPage extends AbstractTestModPage {

	@Test
	public void testVisualEnhancements() {
		compare(
			ModLoader.VISUALENHANCEMENTS,
			getDate(2014, 3, 17),
			"rbray89",
			"EnvironmentalVisualEnhancements-7-3.zip",
			"https://github.com/rbray89/EnvironmentalVisualEnhancements/releases/download/Release-7-3/EnvironmentalVisualEnhancements-7-3.zip",
			"https://avatars2.githubusercontent.com/u/1095572?v=2&s=40"
		);
	}

}
