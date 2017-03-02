package com.bazaarvoice.piiproxy.pii;


import com.bazaarvoice.emodb.sor.api.DataStore;
import com.bazaarvoice.emodb.sor.delta.Delta;
import com.bazaarvoice.piiproxy.Annotations.EmoDataStoreEuWest1Client;
import com.bazaarvoice.piiproxy.Annotations.EmoDataStoreUsEast1Client;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class PiiProxySourceImpl implements PiiProxySource {

    private static final Logger _log = LoggerFactory.getLogger(PiiProxySourceImpl.class);
    private static final String DEFAULT_PLACEMENT = "pii_eu:pii";


    private final DataStore _emoUsEast1Client;
    private final DataStore _emoEuWest1Client;

    @Inject
    public PiiProxySourceImpl(@EmoDataStoreUsEast1Client DataStore emoUsEast1Client, @EmoDataStoreEuWest1Client DataStore emoEuWest1Client) {
        _emoUsEast1Client = emoUsEast1Client;
        _emoEuWest1Client = emoEuWest1Client;
    }

    @Override
    public void createTable(String table, String placement, String options, String audit, String apiKey) {
        checkNotNull(apiKey, "apiKey");

        try {
            if (Strings.isNullOrEmpty(placement)) {
                placement = DEFAULT_PLACEMENT;
            }

            // check if the placement is valid from config values.


            //  get placement for facade
            String placementForFacade = "";

            // get the client for the placement region


            // create the master table in the placement region


            // create the facade in the other region


            // return the ref-id


        } catch (Exception e) {
            _log.error("Failed to create table {}", table);
            Throwables.propagate(e);
        }
    }

    @Override
    public long updateContent(String table, String key, UUID changeId, Delta delta, String locale, String audit, String apiKey) {
        checkNotNull(locale, "locale");
        checkNotNull(apiKey, "apiKey");

        try {

            // get the placement from the specified locale


            // get the placement for the specified table


            // get the client for the table placement region


            // get the delta for master table and the delta for the face.


            // store data in master table


            // store data in facade.


        } catch (Exception e) {
            _log.error("Failed to update content. table: {} key: {} locale: {}", table, key, locale);
            Throwables.propagate(e);
        }

        return 0;
    }

    @Override
    public void getContent(String table, String key, String apiKey) {
        checkNotNull(apiKey, "apiKey");
        try {

            // Based on the PII web-app region, first check the corresponding emodb.


            // If there is a facade in this region and the response body has reference data, then check the other region for the actual data.


            // return the object.


        } catch (Exception e) {
            _log.error("Failed to retrieve content. table: {} key: {}", table, key);
            Throwables.propagate(e);
        }

    }
}