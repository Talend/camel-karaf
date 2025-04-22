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

import static org.apache.camel.builder.Builder.constant;
import static org.apache.camel.component.azure.eventhubs.CredentialType.CONNECTION_STRING;
import static org.apache.camel.component.azure.eventhubs.EventHubsConstants.PARTITION_ID;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import java.time.Duration;
import java.util.function.Function;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.nimbusds.jose.util.Base64;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.azure.eventhubs.EventHubsComponent;
import org.apache.camel.component.azure.eventhubs.EventHubsConfiguration;
import org.apache.camel.model.RouteDefinition;
import org.apache.karaf.camel.itests.AbstractCamelSingleFeatureResultMockBasedRouteSupplier;
import org.apache.karaf.camel.itests.CamelRouteSupplier;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        name = "karaf-camel-azure-eventhubs-test",
        immediate = true,
        service = CamelRouteSupplier.class
)
public class CamelAzureEventhubsRouteSupplier extends AbstractCamelSingleFeatureResultMockBasedRouteSupplier {

    private static final String COMPONENT_NAME = "azure-eventhubsAA";
    public static final String TEST_EVENT_CONTENT = "This is the event content!";

    private static final String DEFAULT_ACCOUNT_NAME = "devstoreaccount1";
    private static final String DEFAULT_ACCOUNT_KEY
            = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";
    private static final Logger log = LoggerFactory.getLogger(CamelAzureEventhubsRouteSupplier.class);

    String connectionString = System.getProperty("eventHubs.connectionString");
    String namespace = System.getProperty("eventHubs.namespace");
    String eventHubName = System.getProperty("eventHubs.eventHubName");

    BlobContainerAsyncClient client;

    @Override
    public void configure(CamelContext camelContext) {
        String azuriteConnectionString = System.getProperty("azurite.connectionString");
        String connectionString = System.getProperty("eventHubs.connectionString");
        String namespace = System.getProperty("eventHubs.namespace");
        String eventHubName = System.getProperty("eventHubs.eventHubName");

        // Create and configure EventHubsConfiguration
        EventHubsConfiguration configuration = new EventHubsConfiguration();
        configuration.setConnectionString(connectionString);
        configuration.setBlobContainerName("sample-container"); // Dummy value for emulator

        StorageSharedKeyCredential credential =
                new StorageSharedKeyCredential(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_KEY);
        configuration.setBlobStorageSharedKeyCredential(credential);



        // start
        client = new BlobContainerClientBuilder()
                .connectionString(azuriteConnectionString)
                .containerName(configuration.getBlobContainerName())
                .credential(configuration.getBlobStorageSharedKeyCredential())
                .buildAsyncClient();

        BlobCheckpointStore checkpointStore = new BlobCheckpointStore(client);
        configuration.setCheckpointStore(checkpointStore);

        //   end



        final EventHubsComponent eventHubsComponent = new EventHubsComponent();
        eventHubsComponent.setConfiguration(configuration);

        camelContext.addComponent(COMPONENT_NAME, eventHubsComponent);
    }


    @Override
    protected Function<RouteBuilder, RouteDefinition> consumerRoute() {
        return builder -> builder
                //.from("azure-eventhubs")
                .fromF("%s://emulatorNs1/eh1", COMPONENT_NAME)
                //.fromF("azure-eventhubs:emulatorNs1/eh1?connectionString=RAW(%s)", connectionString)
                //.fromF("azure-eventhubs:?connectionString=RAW(%s)", connectionString)
                .log("Received Event: ${body}");
    }

    @Override
    protected void configureProducer(RouteBuilder builder, RouteDefinition producerRoute) {
        producerRoute
                .log("connection string: ${body}")
                .process( exchange -> {
                    log.info("Create container - START");
                    client.createIfNotExists().block(Duration.ofMinutes(10)); // create the blob container synchronously
                    log.info("Create container - END");
                })
                .setBody(constant(TEST_EVENT_CONTENT))
                //.to("azure-eventhubs")
                .toF("%s://emulatorNs1/eh1", COMPONENT_NAME)
                //.toF("azure-eventhubs:%s/%s?connectionString=RAW(%s)", namespace, eventHubName, connectionString)
                //.toF("azure-eventhubs:emulatorNs1/eh1?connectionString=RAW(%s)", connectionString)
                //.toF("azure-eventhubs:?connectionString=RAW(%s)", connectionString)
                .log("Sent event : ${body}");
    }
}