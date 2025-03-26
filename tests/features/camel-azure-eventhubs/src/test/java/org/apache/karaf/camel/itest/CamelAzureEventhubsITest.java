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
package org.apache.karaf.camel.itest;

import static org.apache.karaf.camel.test.CamelAzureEventhubsRouteSupplier.TEST_EVENT_CONTENT;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.karaf.camel.itests.AbstractCamelSingleFeatureResultMockBasedRouteITest;
import org.apache.karaf.camel.itests.CamelKarafTestHint;
import org.apache.karaf.camel.itests.GenericContainerResource;
import org.apache.karaf.camel.itests.PaxExamWithExternalResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.testcontainers.azure.AzuriteContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.Network;
import org.testcontainers.azure.EventHubsEmulatorContainer;

@CamelKarafTestHint(externalResourceProvider = CamelAzureEventhubsITest.ExternalResourceProviders.class)
@RunWith(PaxExamWithExternalResource.class)
@ExamReactorStrategy(PerClass.class)
public class CamelAzureEventhubsITest extends AbstractCamelSingleFeatureResultMockBasedRouteITest {

    private static final int AMQP_ORIGINAL_PORT = 5672;
    private static final String DEFAULT_NAMESPACE = "emulatorNs1";
    private static final String DEFAULT_EVENT_HUB_NAME = "eh1";
    private static final String DEFAULT_ACCOUNT_NAME = "devstoreaccount1";
    private static final String DEFAULT_ACCOUNT_KEY
            = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

    @Override
    public void configureMock(MockEndpoint mock) {
        mock.expectedBodiesReceived(TEST_EVENT_CONTENT);
    }

    @Test
    public void testResultMock() throws Exception {
        assertMockEndpointsSatisfied();
    }

    public static final class ExternalResourceProviders {

        static Network network = Network.newNetwork();

        static AzuriteContainer azuriteContainer = new AzuriteContainer("mcr.microsoft.com/azure-storage/azurite:3.31.0")
                .withNetwork(network);


        public static GenericContainerResource<AzuriteContainer> createAzuriteContainer() {
            return new GenericContainerResource(azuriteContainer);
        }

        public static GenericContainerResource<EventHubsEmulatorContainer> createEventHubsEmulatorContainer() {

            EventHubsEmulatorContainer eventHubsEmulatorContainer = new EventHubsEmulatorContainer(
                    "mcr.microsoft.com/azure-messaging/eventhubs-emulator:2.1.0"
            )
                    .acceptLicense()
                    .withNetwork(network)
                    .withAzuriteContainer(azuriteContainer)
                    .withExposedPorts(AMQP_ORIGINAL_PORT)
                    .waitingFor(Wait.forListeningPort());

            return new GenericContainerResource<>(eventHubsEmulatorContainer, resource -> {

                String connectionString = eventHubsEmulatorContainer.getConnectionString();
                // workaround for camel                         "Endpoint=sb://%s:%d;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;EntityPath=%s;UseDevelopmentEmulator=true;",
                connectionString = connectionString + "EntityPath=" + DEFAULT_EVENT_HUB_NAME + ";";
                resource.setProperty("eventHubs.connectionString", connectionString);
                resource.setProperty("eventHubs.namespace", DEFAULT_NAMESPACE);
                resource.setProperty("eventHubs.eventHubName", DEFAULT_EVENT_HUB_NAME);
            });
        }
    }

}