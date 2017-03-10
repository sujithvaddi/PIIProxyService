package com.bazaarvoice.piiproxy.pii;


import com.bazaarvoice.emodb.common.uuid.TimeUUIDs;
import com.bazaarvoice.emodb.sor.api.AuditBuilder;
import com.bazaarvoice.emodb.sor.api.AuthDataStore;
import com.bazaarvoice.emodb.sor.api.FacadeOptionsBuilder;
import com.bazaarvoice.emodb.sor.api.ReadConsistency;
import com.bazaarvoice.emodb.sor.api.TableOptionsBuilder;
import com.bazaarvoice.emodb.sor.api.WriteConsistency;
import com.bazaarvoice.emodb.sor.delta.Delta;
import com.bazaarvoice.emodb.sor.delta.Deltas;
import com.bazaarvoice.piiproxy.Annotations.EmoDataStoreEuWest1Client;
import com.bazaarvoice.piiproxy.Annotations.EmoDataStoreUsEast1Client;
import com.bazaarvoice.piiproxy.PiiProxyConfiguration;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class PiiProxySourceImpl implements PiiProxySource {

    private static final Logger _log = LoggerFactory.getLogger(PiiProxySourceImpl.class);

    private PiiProxyConfiguration _piiProxyConfiguration;
    private final AuthDataStore _emoUsEast1Client;
    private final AuthDataStore _emoEuWest1Client;

    @Inject
    public PiiProxySourceImpl(PiiProxyConfiguration piiProxyConfiguration,
                              @EmoDataStoreUsEast1Client AuthDataStore emoUsEast1Client,
                              @EmoDataStoreEuWest1Client AuthDataStore emoEuWest1Client) {
        _piiProxyConfiguration = checkNotNull(piiProxyConfiguration, "piiProxyConfiguration");
        _emoUsEast1Client = checkNotNull(emoUsEast1Client, "emoUsEast1Client");
        _emoEuWest1Client = checkNotNull(emoEuWest1Client, "emoEuWest1Client");
    }

    /*
       - create table per client or per client and PII-Type or anything ... (example: pii-bvclient or pii-bvclient-ipaddress etc...)
       - master table is always in the system datacenter, and a facade is created in the other datacenter.
     */
    @Override
    public void createTable(String table, String audit, String apiKey) {
        checkNotNull(table, "table");
        checkNotNull(apiKey, "apiKey");


        try {
            // get the appropriate emo clients
            AuthDataStore emoClientForTable;
            AuthDataStore emoClientForFacade;
            if (_piiProxyConfiguration.getSystemDatacenter().equals(_piiProxyConfiguration.getUsEast1Datacenter())) {
                emoClientForTable = _emoUsEast1Client;
                emoClientForFacade = _emoEuWest1Client;
            } else {
                emoClientForTable = _emoEuWest1Client;
                emoClientForFacade = _emoUsEast1Client;
            }

            // get the placement of system data center
            String placementForTable = _piiProxyConfiguration.getEmoDataCenterConfiguration().stream()
                    .filter(p -> p.getDatacenter().equals(_piiProxyConfiguration.getSystemDatacenter())).findFirst().get().getPlacement();

            // get the placement for facade
            String placementForFacade = _piiProxyConfiguration.getEmoDataCenterConfiguration().stream()
                    .filter(p -> !p.getDatacenter().equals(_piiProxyConfiguration.getSystemDatacenter())).findFirst().get().getPlacement();

            // create the master table in the system data center region
            emoClientForTable.createTable(apiKey, table, new TableOptionsBuilder().setPlacement(placementForTable).build(),
                    ImmutableMap.<String, String>of(), new AuditBuilder().setComment("create table -" + audit).build());

            // create the facade in the other region
            emoClientForFacade.createFacade(apiKey, table, new FacadeOptionsBuilder().setPlacement(placementForFacade).build(),
                    new AuditBuilder().setComment("create facade -" + audit).build());

        } catch (Exception e) {
            _log.error("Failed to create table {}", table);
            Throwables.propagate(e);
        }
    }

    /*
       - create or update the content in the datacenter as per the provided locale.
    */
    @Override
    public String updateContent(String table, String key, Map<String, Object> json, String locale, String audit, String apiKey) {
        checkNotNull(table, "table");
        checkNotNull(locale, "locale");
        checkNotNull(audit, "audit");
        checkNotNull(apiKey, "apiKey");

        try {
            // get the datacenter config for locale
            PiiProxyConfiguration.EmoDataCenterConfiguration emoDataCenterConfiguration = _piiProxyConfiguration.getEmoDataCenterConfiguration().stream()
                    .filter(p -> p.getLocales().contains(locale)).findFirst().get();

            // get the client for locale
            AuthDataStore emoClient;
            if (emoDataCenterConfiguration.getDatacenter().equals(_piiProxyConfiguration.getUsEast1Datacenter())) {
                emoClient = _emoUsEast1Client;
            } else {
                emoClient = _emoEuWest1Client;
            }

            // find out if master table or facade exists in the locale datacenter.
            // this is needed as we have to pass in isFacade bool value in our update call.
            boolean isFacade;
            if (emoDataCenterConfiguration.getDatacenter().equals(_piiProxyConfiguration.getSystemDatacenter())) {
                isFacade = false;
            } else {
                isFacade = true;
            }

            // build Delta
            UUID changeId = TimeUUIDs.newUUID();
            Delta delta = Deltas.literal(json);

            // generate key in create case
            if (key == null) {
                key = PiiContentKeyGenerator.piiContentKey(changeId.toString(), locale);
            }

            // update data
            // TODO: Check updateforFacade is not present and we cannot use update always
            // as it can throw Access Denied Exceptions depending on whether it's facade or not!!

            // emoClient.update(apiKey, table, key, changeId, delta,
                    new AuditBuilder().setComment(audit).build(), WriteConsistency.STRONG, isFacade, ImmutableSet.of());

            // return key
            return key;

        } catch (Exception e) {
            _log.error("Failed to update content. table: {} locale: {}", table, locale);
            Throwables.propagate(e);
        }

        return null;
    }

    /*
       - get the content.
    */
    @Override
    public Map<String, Object> getContent(String table, String key, String apiKey) {
        checkNotNull(table, "table");
        checkNotNull(key, "key");
        checkNotNull(apiKey, "apiKey");

        Map<String, Object> content = ImmutableMap.<String, Object>of();
        try {

            // Based on the key provided, figure out the datacenter of the content
            String locale = PiiContentKeyGenerator.keyLocale(key);
            String dataCenter = _piiProxyConfiguration.getEmoDataCenterConfiguration().stream()
                    .filter(p -> p.getLocales().contains(locale)).findFirst().get().getDatacenter();

            // get the client for the datacenter
            AuthDataStore emoClient;
            if (dataCenter.equals(_piiProxyConfiguration.getUsEast1Datacenter())) {
                emoClient = _emoUsEast1Client;
            } else {
                emoClient = _emoEuWest1Client;
            }

            // Do a emodb get
            content = emoClient.get(apiKey, table, key, ReadConsistency.STRONG);

        } catch (Exception e) {
            _log.error("Failed to retrieve content. table: {} key: {}", table, key);
            Throwables.propagate(e);
        }

        return content;
    }
}