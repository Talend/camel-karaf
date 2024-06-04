/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.karaf.component.directvm;

import javax.annotation.processing.Generated;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.ExtendedPropertyConfigurerGetter;
import org.apache.camel.spi.PropertyConfigurerGetter;
import org.apache.camel.spi.ConfigurerStrategy;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.util.CaseInsensitiveMap;
import org.apache.camel.support.component.PropertyConfigurerSupport;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
@Generated("org.apache.camel.maven.packaging.EndpointSchemaGeneratorMojo")
@SuppressWarnings("unchecked")
public class DirectVmComponentConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(CamelContext camelContext, Object obj, String name, Object value, boolean ignoreCase) {
        DirectVmComponent target = (DirectVmComponent) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "autowiredenabled":
        case "autowiredEnabled": target.setAutowiredEnabled(property(camelContext, boolean.class, value)); return true;
        case "block": target.setBlock(property(camelContext, boolean.class, value)); return true;
        case "bridgeerrorhandler":
        case "bridgeErrorHandler": target.setBridgeErrorHandler(property(camelContext, boolean.class, value)); return true;
        case "headerfilterstrategy":
        case "headerFilterStrategy": target.setHeaderFilterStrategy(property(camelContext, org.apache.camel.spi.HeaderFilterStrategy.class, value)); return true;
        case "lazystartproducer":
        case "lazyStartProducer": target.setLazyStartProducer(property(camelContext, boolean.class, value)); return true;
        case "propagateproperties":
        case "propagateProperties": target.setPropagateProperties(property(camelContext, boolean.class, value)); return true;
        case "timeout": target.setTimeout(property(camelContext, long.class, value)); return true;
        default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "autowiredenabled":
        case "autowiredEnabled": return boolean.class;
        case "block": return boolean.class;
        case "bridgeerrorhandler":
        case "bridgeErrorHandler": return boolean.class;
        case "headerfilterstrategy":
        case "headerFilterStrategy": return org.apache.camel.spi.HeaderFilterStrategy.class;
        case "lazystartproducer":
        case "lazyStartProducer": return boolean.class;
        case "propagateproperties":
        case "propagateProperties": return boolean.class;
        case "timeout": return long.class;
        default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        DirectVmComponent target = (DirectVmComponent) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "autowiredenabled":
        case "autowiredEnabled": return target.isAutowiredEnabled();
        case "block": return target.isBlock();
        case "bridgeerrorhandler":
        case "bridgeErrorHandler": return target.isBridgeErrorHandler();
        case "headerfilterstrategy":
        case "headerFilterStrategy": return target.getHeaderFilterStrategy();
        case "lazystartproducer":
        case "lazyStartProducer": return target.isLazyStartProducer();
        case "propagateproperties":
        case "propagateProperties": return target.isPropagateProperties();
        case "timeout": return target.getTimeout();
        default: return null;
        }
    }
}
