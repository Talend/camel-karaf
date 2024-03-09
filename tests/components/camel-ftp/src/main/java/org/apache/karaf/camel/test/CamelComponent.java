/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.camel.test;

import org.apache.camel.builder.RouteBuilder;
import org.apache.karaf.camel.itests.AbstractCamelComponent;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.testcontainers.containers.FixedHostPortGenericContainer;

@Component(
        name = "karaf-camel-ftp-test",
        immediate = true
)
public class CamelComponent extends AbstractCamelComponent {
    private FixedHostPortGenericContainer ftpContainer;

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


    @Override
    protected  void setupResources(ComponentContext context) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(context.getBundleContext().getBundle().adapt(BundleWiring.class).getClassLoader());
            ftpContainer =
                    new FixedHostPortGenericContainer<>("delfer/alpine-ftp-server:latest").withFixedExposedPort(PASSIVE_MODE_PORT,
                                    PASSIVE_MODE_PORT)
                            .withExposedPorts(PORT)
                            .withEnv("USERS", USER + "|" + PASSWORD)
                            .withEnv("MIN_PORT", String.valueOf(PASSIVE_MODE_PORT))
                            .withEnv("MAX_PORT", String.valueOf(PASSIVE_MODE_PORT));
            ftpContainer.start();
        } finally {
             Thread.currentThread().setContextClassLoader(cl);
        }
    }

    @Override
    protected  void tearDownResources() {
        ftpContainer.stop();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // TODO - this will be changes to FTP route
                from("file:/tmp?noop=true&delete=false").log("Received file: ${header.CamelFileName}")
                        .setBody(constant("OK")) // Set the body of the message to "OK"
                        .to("file:" + System.getProperty("project.target")
                                + "?fileName=testResult.txt"); // Write the message to a file;
            }
        };
    }
}