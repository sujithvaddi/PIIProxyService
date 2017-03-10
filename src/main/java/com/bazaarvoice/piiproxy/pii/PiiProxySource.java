package com.bazaarvoice.piiproxy.pii;

import com.bazaarvoice.emodb.sor.delta.Delta;

import java.util.Map;
import java.util.UUID;

/**
 * Defines the interface for interacting with emodb and perform it's datastore operations.
 */
public interface PiiProxySource {

    void createTable(String table, String audit, String apiKey);

    String updateContent(String table, String key, Map<String, Object> json, String locale, String audit, String apiKey);

    Map<String, Object> getContent(String table, String key, String apiKey);
}