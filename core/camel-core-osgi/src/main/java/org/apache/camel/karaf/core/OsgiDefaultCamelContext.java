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
package org.apache.camel.karaf.core;

import org.apache.camel.TypeConverter;
import org.apache.camel.karaf.core.utils.BundleContextUtils;
import org.apache.camel.karaf.core.utils.BundleDelegatingClassLoader;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultRegistry;
import org.osgi.framework.BundleContext;

public class OsgiDefaultCamelContext extends AbstractOsgiDefaultCamelContext {

    public OsgiDefaultCamelContext(BundleContext bundleContext) {
        super(bundleContext);

        // remove the OnCamelContextLifecycleStrategy that camel-core adds by default which does not work well for OSGi
        getLifecycleStrategies().removeIf(l -> l.getClass().getSimpleName().contains("OnCamelContextLifecycleStrategy"));

        // inject common osgi
        OsgiCamelContextHelper.osgiUpdate(this, bundleContext);

        // and these are blueprint specific
        OsgiBeanRepository repo1 = new OsgiBeanRepository(bundleContext);
        getCamelContextExtension().setRegistry(new DefaultRegistry(repo1));
        // Need to clean up the OSGi service when camel context is closed.
        addLifecycleStrategy(repo1);
        // setup the application context classloader with the bundle classloader
        setApplicationContextClassLoader(new BundleDelegatingClassLoader(bundleContext.getBundle()));

        init();
    }

    @Override
    protected TypeConverter createTypeConverter() {
        // CAMEL-3614: make sure we use a bundle context which imports org.apache.camel.impl.converter package
        BundleContext ctx = BundleContextUtils.getBundleContext(getClass());
        if (ctx == null) {
            ctx = getBundleContext();
        }
        return new OsgiTypeConverter(ctx, this, getInjector());
    }

}