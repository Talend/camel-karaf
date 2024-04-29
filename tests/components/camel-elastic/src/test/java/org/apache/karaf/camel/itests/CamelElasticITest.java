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

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.karaf.camel.test.CamelElasticComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.swissbox.tracker.ServiceLookup;
import org.osgi.framework.Constants;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CamelElasticITest extends AbstractCamelKarafResultMockBasedITest {


    private ElasticsearchContainer elasticsearchContainer;

    @Before
    @Override
    public void init() throws Exception {
        CamelElasticComponent component  = ServiceLookup.getService(bundleContext, CamelElasticComponent.class);
        elasticsearchContainer
                = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.1")
                        .withPassword(component.getPassword());
        // Increase the timeout from 60 seconds to 90 seconds to ensure that it will be long enough
        // on the build pipeline
        elasticsearchContainer.setWaitStrategy(
                new LogMessageWaitStrategy()
                        .withRegEx(".*(\"message\":\\s?\"started[\\s?|\"].*|] started\n$)")
                        .withStartupTimeout(Duration.ofSeconds(90)));
        elasticsearchContainer.start();

        elasticsearchContainer.caCertAsBytes().ifPresent(content -> {
            try {
                Files.write(component.getCaFile(), content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        super.init();
    }

    @After
    public void destroyContainer() {
        if (elasticsearchContainer != null) {
            elasticsearchContainer.stop();
        }
    }

    @Override
    protected void configureMock(MockEndpoint mock) {
        mock.expectedBodiesReceived("OK");
    }

    @Test
    public void testResultMock() throws Exception {
        assertMockEndpointsSatisfied();
    }

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.IMPORT_PACKAGE, "org.apache.karaf.camel.test,org.testcontainers.elasticsearch");
        return probe;
    }

}