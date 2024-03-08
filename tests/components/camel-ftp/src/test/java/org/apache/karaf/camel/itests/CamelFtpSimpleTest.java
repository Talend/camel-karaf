package org.apache.karaf.camel.itests;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Ignore
public class CamelFtpSimpleTest {
    private static final int FTP_PORT = 21;
    private static final String FTP_USERNAME = "user";
    private static final String FTP_PASSWORD = "password";
    private static final String FTP_DIRECTORY = "/";

    private static GenericContainer<?> ftpContainer;
    private static FTPClient ftpClient;

    @BeforeClass
    public static void setupFtpServer() {
        ftpContainer = new GenericContainer<>(DockerImageName.parse("stilliard/pure-ftpd"))
                .withExposedPorts(FTP_PORT)
                .withEnv("PUBLICHOST", "localhost")
                .withEnv("FTP_USER_NAME", FTP_USERNAME)
                .withEnv("FTP_USER_PASS", FTP_PASSWORD)
                .withEnv("FTP_USER_HOME", FTP_DIRECTORY);
        ftpContainer.start();

        int mappedPort = ftpContainer.getMappedPort(FTP_PORT);

        ftpClient = new FTPClient();
        try {
            ftpClient.connect("localhost", mappedPort);
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownFtpServer() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ftpContainer != null) {
            ftpContainer.stop();
        }
    }

    @Test
    public void testFileTransferToFTP() {
        try {
            // Create an empty text file
            InputStream inputStream = new ByteArrayInputStream(new byte[0]);

            // Upload the file to FTP server
            boolean uploaded = ftpClient.storeFile("test.txt", inputStream);

            // Verify the file was uploaded successfully
            assertTrue("File was not uploaded to FTP server", uploaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
