/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.test.blueprint;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnExceptionOccurredProcessorTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "org/apache/camel/test/blueprint/OnExceptionOccurredProcessorTest.xml";
    }

    @Test
    public void testOnExceptionOccurred() throws Exception {
        getMockEndpoint("mock:dead").expectedMessageCount(1);

        template.sendBody("direct:start", "Hello World");

        MockEndpoint.assertIsSatisfied(context);

        MyProcessor myProcessor = context.getRegistry().lookupByNameAndType("myProcessor", MyProcessor.class);
        // 1 = first time + 3 redelivery attempts
        assertEquals(1 + 3, myProcessor.getInvoked());
    }

    public static class MyProcessor implements Processor {

        private int invoked;

        @Override
        public void process(Exchange exchange) throws Exception {
            invoked++;
        }

        public int getInvoked() {
            return invoked;
        }
    }

}
