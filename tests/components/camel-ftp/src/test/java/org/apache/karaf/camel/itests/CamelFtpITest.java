/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.camel.itests;


import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.junit.PaxExamServer;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Constants;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CamelFtpITest extends CamelKarafITest {
//    @Rule
//    public PaxExamServer exam = new PaxExamServer();

    private static final int FTP_PORT = 21;
    private static final String FTP_USERNAME = "user";
    private static final String FTP_PASSWORD = "password";
    private static final String FTP_DIRECTORY = "/";

//    public static GenericContainer<?> ftpContainer =
//            new GenericContainer<>(DockerImageName.parse("stilliard/pure-ftpd")).withExposedPorts(FTP_PORT)
//                    .withEnv("PUBLICHOST", "localhost")
//                    .withEnv("FTP_USER_NAME", FTP_USERNAME)
//                    .withEnv("FTP_USER_PASS", FTP_PASSWORD)
//                    .withEnv("FTP_USER_HOME", FTP_DIRECTORY);
    //    //    private static FTPClient ftpClient;
private static final int PORT = 21;
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final int FTP_TIMEOUT_IN_MILLISECONDS = 1000 * 60;

    private static final int PASSIVE_MODE_PORT = 21000;
    private static final FixedHostPortGenericContainer ftpContainer = new FixedHostPortGenericContainer<>(
            "delfer/alpine-ftp-server:latest")
            .withFixedExposedPort(PASSIVE_MODE_PORT, PASSIVE_MODE_PORT)
            .withExposedPorts(PORT)
            .withEnv("USERS", USER + "|" + PASSWORD)
            .withEnv("MIN_PORT", String.valueOf(PASSIVE_MODE_PORT))
            .withEnv("MAX_PORT", String.valueOf(PASSIVE_MODE_PORT));

    //    @After
    //    public void tearDownFtpServer() {
    //        //        if (ftpClient != null && ftpClient.isConnected()) {
    //        //            try {
    //        //                ftpClient.logout();
    //        //                ftpClient.disconnect();
    //        //            } catch (Exception e) {
    //        //                e.printStackTrace();
    //        //            }
    //        //        }
    //        if (ftpContainer != null) {
    //            ftpContainer.stop();
    //        }
    //    }

//        @Configuration
//        public Option[] config() {
//            Option[] testcontainers =
//                    {cleanCaches()
//    //                        mavenBundle().groupId("org.testcontainers").artifactId("testcontainers").versionAsInProject().start()
////                            wrappedBundle(mavenBundle("org.testcontainers", "testcontainers").version("1.16.3").noStart())
//                    };
//
//            return Stream.of(super.config(), testcontainers).flatMap(Stream::of).toArray(Option[]::new);
//            }

//        @ProbeBuilder
//        public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
//            super.probeConfiguration(probe);
//            //make sure the needed imports are there.
//            probe.setHeader(Constants.IMPORT_PACKAGE, "org.testcontainers.containers");
//            return probe;
//        }

    //    @Before
    //    public void setUp() throws Exception {
    //        ftpContainer = new GenericContainer<>(DockerImageName.parse("stilliard/pure-ftpd"))
    //                .withExposedPorts(FTP_PORT)
    //                .withEnv("PUBLICHOST", "localhost")
    //                .withEnv("FTP_USER_NAME", FTP_USERNAME)
    //                .withEnv("FTP_USER_PASS", FTP_PASSWORD)
    //                .withEnv("FTP_USER_HOME", FTP_DIRECTORY);
    //        ftpContainer.start();
    //
    //        int mappedPort = ftpContainer.getMappedPort(FTP_PORT);
    //    }

    //    @After
    //    public void tearDown() throws Exception {
    //        ftpContainer = new GenericContainer<>(DockerImageName.parse("stilliard/pure-ftpd"))
    //                .withExposedPorts(FTP_PORT)
    //                .withEnv("PUBLICHOST", "localhost")
    //                .withEnv("FTP_USER_NAME", FTP_USERNAME)
    //                .withEnv("FTP_USER_PASS", FTP_PASSWORD)
    //                .withEnv("FTP_USER_HOME", FTP_DIRECTORY);
    //        ftpContainer.start();
    //        if (ftpContainer != null) {
    //            ftpContainer.stop();
    //        }
    //    }


    @Test
    public void testFtpComponent() throws Exception {

        System.out.println("HELLLLLO!!!!");
        installBundle("file://"+ getBaseDir() +"/camel-ftp-test-"+ getVersion() + ".jar",true);
        assertBundleInstalled("camel-ftp-test");
        assertBundleInstalledAndRunning("camel-ftp-test");
        ftpContainer.start();
        System.out.println("FTP is created "+ftpContainer.isCreated());
        //        Path filePath  = Path.of(getBaseDir(),"testResult.txt");
        //        Awaitility.await().atMost(5, TimeUnit.SECONDS)
        //                .until(() -> Files.exists(filePath));
        //        assertTrue(Files.exists(filePath));
        //        mavenBundle().groupId("org.testcontainers").artifactId("testcontainers").version("1.16.3").start();
        //        installBundle(wrappedBundle(mavenBundle("org.testcontainers", "testcontainers").version("1.16.3")).getURL(), true);

    }
}