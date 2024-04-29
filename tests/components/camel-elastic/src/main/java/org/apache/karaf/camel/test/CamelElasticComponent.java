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
import java.nio.file.Path;
import java.util.function.Function;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.es.ElasticsearchComponent;
import org.apache.camel.model.RouteDefinition;
import org.apache.karaf.camel.itests.AbstractCamelComponentResultMockBased;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.testcontainers.containers.FixedHostPortGenericContainer;

@Component(name = "karaf-camel-elastic-test", immediate = true)
public class CamelElasticComponent extends AbstractCamelComponentResultMockBased {

    int port = org.apache.karaf.camel.itests.Utils.getNextAvailablePort();

   /*
    @Override
    protected void setupResources(ComponentContext context) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread()
                    .setContextClassLoader(context.getBundleContext().getBundle().adapt(BundleWiring.class).getClassLoader());
            //FIXME : not official images, to change to a service like in the integration test

            ftpContainer = new FixedHostPortGenericContainer<>("docker.elastic.co/elasticsearch/elasticsearch:8.11.1").withFixedExposedPort(PORT, PORT)
                    .withFixedExposedPort(PASSIVE_MODE_PORT, PASSIVE_MODE_PORT)
                    .withFixedExposedPort(PASSIVE_MODE_PORT + 1, PASSIVE_MODE_PORT + 1)
                    .withFixedExposedPort(PASSIVE_MODE_PORT + 2, PASSIVE_MODE_PORT + 2)
                    .withEnv("USERS", USER + "|" + PASSWORD + "|" + FTP_DIRECTORY)
                    .withEnv("MIN_PORT", String.valueOf(PASSIVE_MODE_PORT))
                    .withEnv("MAX_PORT", String.valueOf(PASSIVE_MODE_PORT + 2))
                    .withEnv("PUBLICHOST", "localhost")
                    .withEnv("FTP_USER_NAME", FTP_USERNAME)
                    .withEnv("FTP_USER_PASS", FTP_PASSWORD)
                    .withEnv("FTP_USER_HOME", FTP_DIRECTORY);
            ftpContainer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }*/

    private static final String USER_NAME = "elastic";
    private static final String PASSWORD = "s3cret";

    public String getPassword() {
        return PASSWORD;
    }

    public Path getCaFile(){
        return Path.of(getBaseDir(),"http_ca.crt");
    }


    @Override
    protected void configureCamelContext(CamelContext camelContext) {
        final ElasticsearchComponent elasticsearchComponent = new ElasticsearchComponent();
        elasticsearchComponent.setEnableSSL(true);
        elasticsearchComponent.setHostAddresses("localhost:%s".formatted(port));
        elasticsearchComponent.setUser(USER_NAME);
        elasticsearchComponent.setPassword(PASSWORD);
        elasticsearchComponent.setCertificatePath("file:%s".formatted(getCaFile()));

        camelContext.addComponent("elasticsearch",elasticsearchComponent);
    }


    @Override
    protected void configureProducer(RouteBuilder builder, RouteDefinition producerRoute) {


        //uploads a file "test.txt" containing "OK"
        producerRoute.to("elasticsearch://elasticsearch?operation=Exists&amp;indexName={{elastic.index}}")
                .log("Index exist: ${body}")
                .setBody(builder.simple("""
                        {"date": "${header.CamelTimerFiredTime}", "someKey": "someValue"}
                        """))
                .to("elasticsearch://elasticsearch?operation=Index&indexName={{elastic.index}}")
                .log("Index doc : ${body}")
                .setHeader("_ID", builder.simple("${body}"))
                .to("elasticsearch://elasticsearch?operation=GetById&indexName={{elastic.index}}")
                .log("Get doc: ${body}")
                .setHeader("indexId", builder.simple("${header._ID}"))
                .setBody(builder.constant("""
                        {"doc": {"someKey": "someValue2"}}
                        """))
                .to("elasticsearch://elasticsearch?operation=Update&indexName={{elastic.index}}")
                .log("Update doc: ${body} ")
                .setBody(builder.simple("${header._ID}"))
                .to("elasticsearch://elasticsearch?operation=GetById&indexName={{elastic.index}}")
                .log("Get doc: ${body}")
                .setBody(builder.simple("${header._ID}"))
                .to("elasticsearch://elasticsearch?operation=Delete&indexName={{elastic.index}}")
                .log("Delete doc: ${body}");

    }

    @Override
    protected boolean consumerEnabled() {
        return false;
    }
}