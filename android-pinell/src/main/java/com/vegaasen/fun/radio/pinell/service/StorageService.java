package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.List;

/**
 * Service that relates to storing the various hosts in a data-storage. This provides functionality that enables:
 * - Storing selected (valid) hosts
 * - Removing existing (stored) hosts
 * - Clear all existing (stored) hosts
 * - Get all (stored) hosts
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @version 1.0
 * @see com.vegaasen.fun.radio.pinell.context.ApplicationContext
 * @since 05.01.2016@1.0
 */
public interface StorageService {

    /**
     * Reset the full cache that already exists within the storage
     */
    int clear();

    /**
     * Verifies if the provided host exists within the storage
     *
     * @param host _
     */
    boolean contains(Host host);

    /**
     * Remove the provided hostBean from the storage
     *
     * @param hostBean _
     */
    void remove(Host hostBean);

    /**
     * Store the specific host that has been chosen
     *
     * @param candidate _
     */
    void store(Host candidate);

    /**
     * Fetch all of the registered hosts
     *
     * @return _
     */
    List<Host> getAll();

}
