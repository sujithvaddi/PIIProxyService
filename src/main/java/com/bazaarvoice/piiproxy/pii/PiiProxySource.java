package com.bazaarvoice.piiproxy.pii;

import com.bazaarvoice.emodb.sor.delta.Delta;

import java.util.UUID;

/**
 * Defines the interface for interacting with emodb and perform it's datastore operations.
 */
public interface PiiProxySource {

    void createTable(String table, String placement, String options, String audit, String apiKey);

    long updateContent(String table, String key, UUID changeId, Delta delta, String locale, String audit, String apiKey);

    void getContent(String table, String key, String apiKey);
}