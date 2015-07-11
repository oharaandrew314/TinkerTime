package io.andrewohara.tinkertime;

import io.andrewohara.tinkertime.io.crawlers.TestCurseCrawler;
import io.andrewohara.tinkertime.io.crawlers.TestGithubCrawler;
import io.andrewohara.tinkertime.io.crawlers.TestJenkinsCrawler;
import io.andrewohara.tinkertime.io.crawlers.TestKerbalStuffCrawler;
import io.andrewohara.tinkertime.workflows.tasks.TestAnalyzeModZipTask;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestCurseCrawler.class,
	TestJenkinsCrawler.class,
	TestGithubCrawler.class,
	TestKerbalStuffCrawler.class,
	TestAnalyzeModZipTask.class,
	TestDatabaseMigrationIntegration.class
})

public class UnitTestSuite {}