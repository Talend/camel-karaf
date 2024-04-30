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
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CamelElasticsearchITest extends AbstractCamelKarafResultMockBasedITest {

    private static final String USER_NAME = "elastic";
    private static final String PASSWORD = "s3cret";
    private static final int ELASTIC_SEARCH_PORT = 9200;

    public String getCaFile(){
        return Path.of(getBaseDir(),"http_ca.crt").toAbsolutePath().toString();
    }

    @Configuration
    @Override
    public Option[] config() {

        Option[] options = initContainer().entrySet().stream()
                .map(e -> CoreOptions.systemProperty(e.getKey()).value(e.getValue()))
                .toArray(Option[]::new);
        return Stream.of(super.config(), options).flatMap(Stream::of).toArray(Option[]::new);
    }


    public Map<String,String> initContainer()  {
        final ElasticsearchContainer elasticsearchContainer =
                new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.1").withPassword(PASSWORD);
        // Increase the timeout from 60 seconds to 90 seconds to ensure that it will be long enough
        // on the build pipeline
        elasticsearchContainer.setWaitStrategy(
                new LogMessageWaitStrategy()
                        .withRegEx(".*(\"message\":\\s?\"started[\\s?|\"].*|] started\n$)")
                        .withStartupTimeout(Duration.ofSeconds(90)));

        elasticsearchContainer.start();

        elasticsearchContainer.caCertAsBytes().ifPresent(content -> {
            try {
                Files.write(Path.of(getCaFile()), content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Map<String,String> properties = new HashMap<>();
        properties.put("elasticsearch.host", elasticsearchContainer.getHost());
        properties.put("elasticsearch.port", Integer.toString(elasticsearchContainer.getMappedPort(ELASTIC_SEARCH_PORT)));
        properties.put("elasticsearch.username", USER_NAME);
        properties.put("elasticsearch.password", PASSWORD);
        properties.put("elasticsearch.cafile", getCaFile());
        return properties;
    }

    //FIXME : find a way to destroy the container
   // @AfterClass
    /*public void destroyContainer() {
        if (elasticsearchContainer != null) {
            elasticsearchContainer.stop();
        }
    }
*/
    @Override
    protected void configureMock(MockEndpoint mock) {
        mock.expectedBodiesReceived("OK");
    }

    @Test
    public void testResultMock() throws Exception {
        assertMockEndpointsSatisfied();
    }

}