package aohara.tinkertime.crawlers;


public class TestGithubJsonCrawler extends AbstractTestGithubCrawler {
	
	@Override
	protected boolean isTestingFallbackCrawler() {
		return false;
	}

}
