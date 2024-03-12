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

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CamelJettyITest extends CamelKarafITest {


    @Test
    public void testJettyServerComponent() throws Exception {
        installAndAssertFeature("camel-jetty");
        installBundle("file://"+ getBaseDir() +"/camel-jetty-test-"+ getVersion() + ".jar",true);
        assertBundleInstalled("camel-jetty-test");
        assertBundleInstalledAndRunning("camel-jetty-test");
        HttpClient client = HttpClient.newHttpClient();

        // Create a URI for the request
        URI uri = URI.create("http://localhost:30001/test");

        // Create a HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join(); // Wait for the response

        Path filePath  = Path.of(getBaseDir(),"testResult.txt");
        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .until(() -> Files.exists(filePath));
        assertTrue(Files.exists(filePath));

    }

}