package aohara.tinkertime.testSuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import aohara.tinkertime.TestMod;
import aohara.tinkertime.TestModStateManager;
import aohara.tinkertime.TestModStructure;
import aohara.tinkertime.TestModuleManagerCrawler;
import aohara.tinkertime.TestZipTreeBuilder;
import aohara.tinkertime.controllers.TestModManager;
import aohara.tinkertime.crawlers.TestCrawlerFactory;
import aohara.tinkertime.crawlers.TestCurseCrawler;
import aohara.tinkertime.crawlers.TestGithubHtmlCrawler;
import aohara.tinkertime.crawlers.TestGithubJsonCrawler;
import aohara.tinkertime.crawlers.TestKerbalStuffCrawler;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestCurseCrawler.class,
   TestModManager.class,
   TestModStateManager.class,
   TestModuleManagerCrawler.class,
   TestGithubHtmlCrawler.class,
   TestGithubJsonCrawler.class,
   TestCrawlerFactory.class,
   TestZipTreeBuilder.class,
   TestMod.class,
   TestModStructure.class,
   TestKerbalStuffCrawler.class
})

public class UnitTestSuite {}