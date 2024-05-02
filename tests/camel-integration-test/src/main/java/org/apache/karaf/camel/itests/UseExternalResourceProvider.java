package org.apache.karaf.camel.itests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the class that provides the methods to create all the external resources required by the test.
 * In the provider class, each public static method that returns an instance of a subtype of {@link ExternalResource}
 * with no parameters is considered as an {@link ExternalResource} supplier, so it will be invoked before executing
 * the test and {@code PaxExamWithExternalResource} will ensure that t.
 *
 * @see PaxExamWithExternalResource
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface UseExternalResourceProvider {
    /**
     * The external resource provider class.
     */
    Class<?> value();
}
