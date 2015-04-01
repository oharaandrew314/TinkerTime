package aohara.tinkertime;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import aohara.tinkertime.crawlers.TestCrawlerFactory;
import aohara.tinkertime.crawlers.TestCurseCrawler;
import aohara.tinkertime.crawlers.TestGithubHtmlCrawler;
import aohara.tinkertime.crawlers.TestGithubJsonCrawler;
import aohara.tinkertime.crawlers.TestKerbalStuffCrawler;
import aohara.tinkertime.crawlers.TestJenkinsCrawler;
import aohara.tinkertime.resources.TestModLoader;
import aohara.tinkertime.resources.TestModStructure;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestCurseCrawler.class,
   TestModLoader.class,
   TestJenkinsCrawler.class,
   TestGithubHtmlCrawler.class,
   TestGithubJsonCrawler.class,
   TestCrawlerFactory.class,
   TestModStructure.class,
   TestKerbalStuffCrawler.class
})

public class UnitTestSuite {}