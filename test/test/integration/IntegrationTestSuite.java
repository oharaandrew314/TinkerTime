package test.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestDownloadLinkFormatting.class,
   TestModStructure.class
})

public class IntegrationTestSuite {}