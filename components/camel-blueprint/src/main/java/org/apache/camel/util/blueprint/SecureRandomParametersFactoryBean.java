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
package org.apache.camel.util.blueprint;

import java.util.Set;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import org.apache.camel.CamelContext;
import org.apache.camel.blueprint.BlueprintCamelContextLookupHelper;
import org.apache.camel.core.xml.util.jsse.AbstractSecureRandomParametersFactoryBean;
import org.osgi.service.blueprint.container.BlueprintContainer;

@XmlRootElement(name = "secureRandomParameters")
public class SecureRandomParametersFactoryBean extends AbstractSecureRandomParametersFactoryBean {

    @XmlTransient
    private BlueprintContainer blueprintContainer;

    public void setBlueprintContainer(BlueprintContainer blueprintContainer) {
        this.blueprintContainer = blueprintContainer;
    }

    @Override
    protected CamelContext getCamelContextWithId(String camelContextId) {
        if (blueprintContainer != null) {
            return (CamelContext) blueprintContainer.getComponentInstance(camelContextId);
        }
        return null;
    }

    @Override
    protected CamelContext discoverDefaultCamelContext() {
        if (blueprintContainer != null) {
            Set<String> ids = BlueprintCamelContextLookupHelper.lookupBlueprintCamelContext(blueprintContainer);
            if (ids.size() == 1) {
                // there is only 1 id for a BlueprintCamelContext so fallback and use this
                return getCamelContextWithId(ids.iterator().next());
            }
        }
        return null;
    }

}