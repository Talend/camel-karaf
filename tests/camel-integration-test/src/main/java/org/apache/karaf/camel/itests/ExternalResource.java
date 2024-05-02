package org.apache.karaf.camel.itests;

import java.util.Map;

/**
 * An interface representing an external resource to set up before a test and guarantee to tear it down afterward.
 * Compared to an {@link org.junit.rules.ExternalResource}, the methods {@link #before()} and {@link #after()} are
 * executed outside Karaf container, so it can be used to set up external resources like a database, a message broker,
 * etc.
 * @see TemporaryFile
 * @see GenericContainerResource
 */
public interface ExternalResource {

    /**
     * Sets up the external resource.
     */
    void before();

    /**
     * Tears down the external resource.
     */
    void after();

    /**
     * Gives access to the properties of the external resource like a username, a password or a path, that will be
     * provided to the Karaf instance as System properties.
     */
    Map<String, String> properties();
}
