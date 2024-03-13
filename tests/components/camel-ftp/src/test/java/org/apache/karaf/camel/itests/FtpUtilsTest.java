package org.apache.karaf.camel.itests;

import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ops4j.pax.logging.slf4j.Slf4jMDCAdapter;
import org.slf4j.MDC;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
@Ignore
public class FtpUtilsTest {

    private static final int PORT = 21;
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final int FTP_TIMEOUT_IN_MILLISECONDS = 1000 * 60;

    private static final int PASSIVE_MODE_PORT = 21000;

    private static final GenericContainer ftp = new GenericContainer<>(
            "delfer/alpine-ftp-server:latest")
            .withExposedPorts(PORT)
            .withEnv("USERS", USER + "|" + PASSWORD)
            .withEnv("MIN_PORT", String.valueOf(PASSIVE_MODE_PORT))
            .withEnv("MAX_PORT", String.valueOf(PASSIVE_MODE_PORT));


    @Before
    public void staticSetup() throws IOException {

        ftp.start();
    }

    @After
    public void afterAll() {
        ftp.stop();
    }

    @Test
    public void test() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setDataTimeout(FTP_TIMEOUT_IN_MILLISECONDS);
        ftpClient.setConnectTimeout(FTP_TIMEOUT_IN_MILLISECONDS);
        ftpClient.setDefaultTimeout(FTP_TIMEOUT_IN_MILLISECONDS);

        // Log
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        // Connect
        try {
            ftpClient.connect("localhost", ftp.getMappedPort(PORT));
            ftpClient.setSoTimeout(FTP_TIMEOUT_IN_MILLISECONDS);

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new AssertionError();
            }

            // Login
            boolean loginSuccess = ftpClient.login(USER, PASSWORD);
            if (!loginSuccess) {
                throw new AssertionError();
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

        } catch (IOException e) {
            throw new AssertionError(e);
        }

        String remoteFile = "fileonftp";
        try (InputStream targetStream = new ByteArrayInputStream("Hello FTP".getBytes())) {
            assertThat("FTP is connected", ftpClient.isConnected());
            ftpClient.storeFile(remoteFile, targetStream);
        }
        System.out.println(ftp.execInContainer("/bin/touch", "/testFtp.txt"));

        // Create Camel context
        CamelContext context = new DefaultCamelContext();

        // Define routes
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("ftp://user:password@localhost:21/?fileName=fileonftp"
                        + "&passiveMode=true&binary=true")
                        .to("file:~/");
            }
        });

        // Start Camel context
        context.start();

        // Allow the application to run for a while before shutting down
        Thread.sleep(5000);

        // Stop Camel context
        context.stop();
    }
}