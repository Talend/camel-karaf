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

import java.io.IOException;

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
public class CamelFtpComponent extends AbstractCamelComponent {
    private FixedHostPortGenericContainer ftpContainer;

    private static final String FTP_USERNAME = "user";
    private static final String FTP_PASSWORD = "password";
    private static final String FTP_DIRECTORY = "/home/user/dir";

    private static final int PORT = 21;
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    private static final int PASSIVE_MODE_PORT = 21100;


    @Override
    protected  void setupResources(ComponentContext context) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(context.getBundleContext().getBundle().adapt(BundleWiring.class).getClassLoader());
            ftpContainer =
                    new FixedHostPortGenericContainer<>("delfer/alpine-ftp-server:latest")
                            .withFixedExposedPort(PORT,PORT)
                            .withFixedExposedPort(PASSIVE_MODE_PORT,PASSIVE_MODE_PORT)
                            .withFixedExposedPort(PASSIVE_MODE_PORT+1,PASSIVE_MODE_PORT+1)
                            .withFixedExposedPort(PASSIVE_MODE_PORT+2,PASSIVE_MODE_PORT+2)
                            .withEnv("USERS", USER + "|" + PASSWORD+"|"+FTP_DIRECTORY)
                            .withEnv("MIN_PORT", String.valueOf(PASSIVE_MODE_PORT))
                            .withEnv("MAX_PORT", String.valueOf(PASSIVE_MODE_PORT+2))
                                .withEnv("PUBLICHOST", "localhost")
                                .withEnv("FTP_USER_NAME", FTP_USERNAME)
                                .withEnv("FTP_USER_PASS", FTP_PASSWORD)
                                .withEnv("FTP_USER_HOME", FTP_DIRECTORY);
            ftpContainer.start();
            Thread.sleep(5000);
            System.out.println("creating a file");
            System.out.println(ftpContainer.execInContainer("/bin/touch", "/home/user/dir/testFtp.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch ( Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
        finally {
             Thread.currentThread().setContextClassLoader(cl);
        }
    }

    @Override
    protected  void tearDownResources() {
        ftpContainer.stop();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        try {

            System.out.println(ftpContainer.execInContainer("/bin/echo", "'11111'", ">", "testFtp.txt"));
            System.out.println(ftpContainer.execInContainer("/bin/ls", "/home"));
            System.out.println(ftpContainer.execInContainer("/bin/pwd"));
//            Thread.sleep(500);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

//                    from("file:" + filePath.toString()
//                                    + "?fileName=testFtp.txt")
//                            .log("Sent file: ${header.CamelFileName}")
//                            .to(route2).
//                                    from("ftp://localhost:21?fileName=ttcouh.txt&username=user&password=password&throwExceptionOnConnectFailed=true&maximumReconnectAttempts=0&stepwise=false&fastExistsCheck=true&disconnect=true&passiveMode=true&noop=true")
//                    .to("file:" + System.getProperty("project.target") + "?fileName=ttcouh.txt")
//                            .log("Downloaded file ${file:name} complete.");

//                from("ftp://localhost:21/?fileName=ttcouh.txt&username=user&password=password&throwExceptionOnConnectFailed=true&maximumReconnectAttempts=0&stepwise=false&fastExistsCheck=true&disconnect=true&passiveMode=true&noop=true")
//                        .to("file:" + System.getProperty("project.target"))
//                        .log("Downloaded file ${file:name} complete.");

                /*from("ftp://user@localhost?password=password&fileName=/home/testFtp.txt&noop=true&passiveMode=true&binary=true")
                        .log("connected to FTP")
                        .to("file://"+System.getProperty("project.target"));*/

                from("ftp://localhost/?username=user&password=password&binary=true&passiveMode=true&delay=1000")
                        .log("ftp connected")
//                        .pollEnrich("ftp://localhost/home/testFtp.txt")
                        //.log("ftp download")
                        .to("file:"+System.getProperty("project.target"));
            }
        };
    }
}