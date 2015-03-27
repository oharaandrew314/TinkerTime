package aohara.tinkertime.testSuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import aohara.tinkertime.TestDownloadLinkFormatting;
import aohara.tinkertime.TestModStructure;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestDownloadLinkFormatting.class,
   TestModStructure.class
})

public class IntegrationTestSuite {}