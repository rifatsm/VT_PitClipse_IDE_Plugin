/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.pitest.pitclipse.example.ExampleTest;
import org.pitest.pitclipse.runner.PitOptions.PitLaunchException;
import org.pitest.pitclipse.runner.util.PitFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.toHexString;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PitOptionsTest {

    private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String TEST_CLASS1 = PitOptionsTest.class.getCanonicalName();
    private static final String TEST_CLASS2 = PitRunner.class.getCanonicalName();
    private static final List<String> CLASS_PATH = ImmutableList.of(TEST_CLASS1, TEST_CLASS2);
    private static final String PACKAGE_1 = PitOptionsTest.class.getPackage().getName() + ".*";
    private static final String PACKAGE_2 = ExampleTest.class.getPackage().getName() + ".*";
    private static final List<String> PACKAGES = ImmutableList.of(PACKAGE_1, PACKAGE_2);
    private static final File REALLY_BAD_PATH = getBadPath();
    private static final Random RANDOM = new Random();

    private final File historyLocation = randomFile();
    private final File testTmpDir = randomDir();
    private final File testSrcDir = randomDir();
    private final File anotherTestSrcDir = randomDir();

    private static File getBadPath() {
        if (IS_OS_WINDOWS) {
            return new File("BAD_DRIVE:\\");
        }
        return new File("/HOPEFULLY/DOES/NOT/EXIST/SO/IS/BAD/");
    }

    @Before
    public void setUp() {
        for (File dir : ImmutableList.of(testTmpDir, testSrcDir, anotherTestSrcDir)) {
            assertTrue("Could not create directories for test", dir.mkdirs());
            dir.deleteOnExit();
        }
    }

    @Test(expected = PitLaunchException.class)
    public void defaultOptionsThrowException() throws IOException {
        PitOptions.builder().build();
    }

    @Test(expected = PitLaunchException.class)
    public void validSourceDirButNoTestClassThrowsException() throws IOException {
        PitOptions.builder().withSourceDirectory(testSrcDir).build();
    }

    @Test
    public void minimumOptionsSet() throws IOException {
        PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
                .build();
        File reportDir = options.getReportDirectory();
        assertTrue(reportDir.isDirectory());
        assertTrue(reportDir.exists());
        assertEquals(TMP_DIR, reportDir.getParentFile());
        assertEquals(TEST_CLASS1, options.getClassUnderTest());
    }

    @Test
    public void stringRepresentation() throws IOException {
        PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
                .build();
        assertThat(options.toString(),
            containsString("classUnderTest=org.pitest.pitclipse.runner.PitOptionsTest"));
    }

    @Test(expected = PitLaunchException.class)
    public void sourceDirectoryDoesNotExist() throws IOException {
        PitOptions.builder().withSourceDirectory(randomDir()).withClassUnderTest(TEST_CLASS1).build();
    }

    @Test(expected = PitLaunchException.class)
    public void multipleSourceDirectoriesOneDoesNotExist() throws IOException {
        PitOptions.builder()
                  .withSourceDirectories(ImmutableList.of(testSrcDir, randomDir()))
                  .withClassUnderTest(TEST_CLASS1)
                  .build();
    }

    @Test(expected = PitLaunchException.class)
    public void useInvalidReportDirectory() {
        PitOptions.builder().withReportDirectory(REALLY_BAD_PATH).withSourceDirectory(testSrcDir)
                .withClassUnderTest(TEST_CLASS1).build();
    }

    @Test
    public void specifyReportDirToCreate() {
        PitOptions options = PitOptions.builder()
                .withSourceDirectory(testSrcDir)
                .withClassUnderTest(TEST_CLASS1)
                .withReportDirectory(randomDir())
                .build();
        File reportDir = options.getReportDirectory();
        assertTrue(reportDir.isDirectory());
        assertTrue(reportDir.exists());
        assertEquals(TMP_DIR, reportDir.getParentFile());
    }

    @Test(expected = PitLaunchException.class)
    public void useInvalidSourceDirectory() {
        PitOptions.builder().withSourceDirectory(REALLY_BAD_PATH).withClassUnderTest(TEST_CLASS1).build();
    }

    @Test
    public void useClasspath() throws IOException {
        PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
                .withClassesToMutate(CLASS_PATH).build();
        File reportDir = options.getReportDirectory();
        assertTrue(reportDir.isDirectory());
        assertTrue(reportDir.exists());
        assertEquals(TMP_DIR, reportDir.getParentFile());
        assertEquals(TEST_CLASS1, options.getClassUnderTest());
    }

    @Test
    public void testPackagesSupplied() {
        PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withPackagesToTest(PACKAGES)
                .withClassesToMutate(CLASS_PATH).build();
        File reportDir = options.getReportDirectory();
        assertTrue(reportDir.isDirectory());
        assertTrue(reportDir.exists());
        assertEquals(TMP_DIR, reportDir.getParentFile());
        assertNull(options.getClassUnderTest());
        assertEquals(PACKAGES, options.getPackages());
    }

    @Test(expected = PitLaunchException.class)
    public void invalidHistoryFileSupplied() {
        PitOptions.builder()
            .withSourceDirectory(testSrcDir)
            .withPackagesToTest(PACKAGES)
            .withClassesToMutate(CLASS_PATH)
            .withHistoryLocation(getBadPath())
            .build();
    }

    @Test(expected = PitLaunchException.class)
    public void invalidHistoryFileSuppliedWithNullParent() {
        // this happens in Windows with a bad path
        File badPath = mock(File.class);
        PitOptions.builder()
            .withSourceDirectory(testSrcDir)
            .withPackagesToTest(PACKAGES)
            .withClassesToMutate(CLASS_PATH)
            .withHistoryLocation(badPath)
            .build();
    }

    @Test
    public void validHistoryLocationSupplied() {
        PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withPackagesToTest(PACKAGES)
                .withClassesToMutate(CLASS_PATH).withHistoryLocation(historyLocation).build();
        File location = options.getHistoryLocation();
        assertFalse(location.isDirectory());
        assertTrue(location.getParentFile().exists());
    }

    @Test
    public void validHistoryLocationSuppliedWithExistingParentDir() throws IOException {
        PitFileUtils.createParentDirs(historyLocation);
        PitOptions options = PitOptions.builder()
                .withSourceDirectory(testSrcDir)
                .withPackagesToTest(PACKAGES)
                .withClassesToMutate(CLASS_PATH)
                .withHistoryLocation(historyLocation)
                .build();
        File location = options.getHistoryLocation();
        assertFalse(location.isDirectory());
        assertTrue(location.getParentFile().exists());
    }

    private String randomString() {
        return toHexString(RANDOM.nextInt());
    }

    private File randomDir() {
        File randomDir = new File(TMP_DIR, randomString());
        randomDir.deleteOnExit();
        return randomDir;
    }

    private File randomFile() {
        File randomFile = new File(randomDir(), randomString());
        randomFile.deleteOnExit();
        return randomFile;
    }
}
