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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@CamelKarafTestHint(externalResourceProvider = CamelAzureEventhubsITest.ExternalResourceProviders.class)
@RunWith(PaxExamWithExternalResource.class)
@ExamReactorStrategy(PerClass.class)
public class CamelAzureEventhubsITest extends AbstractCamelSingleFeatureResultMockBasedRouteITest {

    private static final int AMQP_ORIGINAL_PORT = 5672;
    private static final String DEFAULT_NAMESPACE = "emulatorNs1";
    private static final String DEFAULT_EVENT_HUB_NAME = "eh1";

    @Override
    public void configureMock(MockEndpoint mock) {
        mock.expectedBodiesReceived(TEST_EVENT_CONTENT);
    }

    @Test
    public void testResultMock() throws Exception {
        assertMockEndpointsSatisfied();
    }

    public static final class ExternalResourceProviders {

        public static GenericContainerResource<EventHubsContainer> createAzureStorageQueueContainer() {

            EventHubsContainer eventHubsContainer = new EventHubsContainer("mcr.microsoft.com/azure-messaging/eventhubs-emulator:2.1.0");

            return new GenericContainerResource<>(eventHubsContainer, resource -> {

                String connectionString = String.format(
                        "Endpoint=sb://%s:%d;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;EntityPath=%s;UseDevelopmentEmulator=true;",
                        eventHubsContainer.getHost(), eventHubsContainer.getMappedPort(AMQP_ORIGINAL_PORT), DEFAULT_EVENT_HUB_NAME
                );
                resource.setProperty("eventHubs.connectionString", connectionString);
                resource.setProperty("eventHubs.namespace", DEFAULT_NAMESPACE);
                resource.setProperty("eventHubs.eventHubName", DEFAULT_EVENT_HUB_NAME);
            });
        }
    }

    public static class EventHubsContainer extends GenericContainer<EventHubsContainer> {

        public EventHubsContainer(final String containerName) {
            super(containerName);

            withExposedPorts(AMQP_ORIGINAL_PORT)
                    .waitingFor(Wait.forListeningPort());
        }

    }
}