package test.crawlers;


public class TestGithubHtmlCrawler extends AbstractTestGithubCrawler {

	@Override
	protected boolean isTestingFallbackCrawler() {
		return true;
	}
}
