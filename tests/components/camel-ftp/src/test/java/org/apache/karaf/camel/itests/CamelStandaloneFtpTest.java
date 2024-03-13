package org.apache.karaf.camel.itests;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CamelStandaloneFtpTest {

    @Test
    public void testStandaloneFtp() throws Exception {
        // Create Camel context
        CamelContext context = new DefaultCamelContext();

        // Define routes
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("ftp://localhost/?username=user&password=password&binary=true&passiveMode=true&delay=1000")
                        .to("file:~/");
            }
        });

        // Start Camel context
        context.start();

        // Allow the application to run for a while before shutting down
        Thread.sleep(5000);

        // Stop Camel context
        context.stop();
    }

}
