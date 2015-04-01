package aohara.tinkertime.testSuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import aohara.tinkertime.TestModStateManager;
import aohara.tinkertime.TestModStructure;
import aohara.tinkertime.TestModuleManagerCrawler;
import aohara.tinkertime.crawlers.TestCrawlerFactory;
import aohara.tinkertime.crawlers.TestCurseCrawler;
import aohara.tinkertime.crawlers.TestGithubHtmlCrawler;
import aohara.tinkertime.crawlers.TestGithubJsonCrawler;
import aohara.tinkertime.crawlers.TestKerbalStuffCrawler;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestCurseCrawler.class,
   TestModStateManager.class,
   TestModuleManagerCrawler.class,
   TestGithubHtmlCrawler.class,
   TestGithubJsonCrawler.class,
   TestCrawlerFactory.class,
   TestModStructure.class,
   TestKerbalStuffCrawler.class
})

public class UnitTestSuite {}