package org.pitest.pitclipse.ui.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;
import static org.pitest.pitclipse.ui.view.PitView.BLANK_PAGE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitSummaryView;
import org.pitest.pitclipse.ui.view.PitView;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipsePitSummaryViewTest extends AbstractPitclipseSWTBotTest {
    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_BAR_PACKAGE_RESULT = "foo.bar";
    private static final String FOO_CLASS_HEADER = "Foo.java";
    private static final String FOO_CLASS_RESULT_URL_END = FOO_CLASS_HEADER + ".html";
    private static final String INDEX_URL_END = "index.html";
    private static final String INDEX_HEADER = "Pit Test Coverage Report";
    private PitSummaryView summaryView;

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        importTestProject(TEST_PROJECT);
    }

    @Before
    public void resetView() throws InterruptedException {
        closeViewById(PitView.VIEW_ID);
        openViewById(PitView.VIEW_ID);
        summaryView = PAGES.getPitSummaryView();
    }

    @Test
    public void navigateWithButtons() throws CoreException {
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(2, 80, 0, 6, 0);

        assertThat(summaryView.getCurrentBrowserUrl(), endsWith(INDEX_URL_END));
        final String relativeUrl = summaryView.getCurrentBrowserUrl().replace("/index.html",
                "/" + FOO_BAR_PACKAGE_RESULT + "/" + FOO_CLASS_RESULT_URL_END);
        assertThat(summaryView.setBrowserUrl(relativeUrl), endsWith(FOO_CLASS_RESULT_URL_END));
        assertThat(summaryView.clickBack(INDEX_HEADER), endsWith(INDEX_URL_END));
        assertThat(summaryView.clickForward(FOO_CLASS_HEADER), endsWith(FOO_CLASS_RESULT_URL_END));
        // forward again should not change url
        assertThat(summaryView.clickForward(FOO_CLASS_HEADER), endsWith(FOO_CLASS_RESULT_URL_END));
        assertThat(summaryView.clickHome(INDEX_HEADER), endsWith(INDEX_URL_END));
    }

    @Test
    public void emptyViewTests() {
        // should not change page
        assertThat(summaryView.clickHome(BLANK_PAGE), equalTo(BLANK_PAGE));
        assertThat(summaryView.clickForward(BLANK_PAGE), equalTo(BLANK_PAGE));
        assertThat(summaryView.clickBack(BLANK_PAGE), equalTo(BLANK_PAGE));
    }
}
