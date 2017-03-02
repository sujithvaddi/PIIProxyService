package com.bazaarvoice.piiproxy;

import com.bazaarvoice.emodb.common.dropwizard.discovery.PayloadBuilder;
import com.bazaarvoice.emodb.sor.api.DataStore;
import com.bazaarvoice.emodb.sor.client.DataStoreClient;
import com.bazaarvoice.emodb.sor.client.DataStoreClientFactory;
import com.bazaarvoice.ostrich.ServiceEndPoint;
import com.bazaarvoice.ostrich.ServiceEndPointBuilder;
import com.bazaarvoice.ostrich.ServiceFactory;
import com.bazaarvoice.ostrich.discovery.FixedHostDiscovery;
import com.bazaarvoice.ostrich.pool.ServicePoolBuilder;
import com.bazaarvoice.ostrich.retry.ExponentialBackoffRetry;
import com.bazaarvoice.piiproxy.Annotations.EmoDataStoreEuWest1Client;
import com.bazaarvoice.piiproxy.Annotations.EmoDataStoreUsEast1Client;
import com.bazaarvoice.piiproxy.Resource.PIIProxyResource1;
import com.bazaarvoice.piiproxy.pii.PiiProxySource;
import com.bazaarvoice.piiproxy.pii.PiiProxySourceImpl;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.sun.jersey.api.client.Client;
import io.dropwizard.setup.Environment;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.TimeUnit;


public class PiiProxyModule extends AbstractModule {

    private final Environment _environment;
    private final PiiProxyConfiguration _configuration;
    private final String _name;

    PiiProxyModule(final Environment environment, final PiiProxyConfiguration configuration, final String name) {
        this._environment = environment;
        this._configuration = configuration;
        this._name = name;
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();

        bind(String.class).annotatedWith(Names.named("appName")).toInstance(_name);

        bind(PiiProxyConfiguration.class).toInstance(_configuration);

        bind(MetricRegistry.class).toInstance(_environment.metrics());

        bind(PiiProxySource.class).to(PiiProxySourceImpl.class).asEagerSingleton();
        bind(PIIProxyResource1.class).asEagerSingleton();
    }

    /**
     * Provides a us-east-1 Emo client.
     */
    @Provides
    @Singleton
    @EmoDataStoreUsEast1Client
    DataStore provideEmoUsEastClient(Client jerseyClient, @Named ("AdminKey") String apiKey, MetricRegistry metricRegistry) {

        ServiceFactory<DataStore> clientFactory = DataStoreClientFactory
                .forClusterAndHttpClient(_configuration.getCluster(), jerseyClient)
                .usingCredentials(apiKey);

        URI uri = UriBuilder.fromPath(_configuration.getEmoDataCenterConfiguration().getUsEastUri()).build();

        ServiceEndPoint endPoint = new ServiceEndPointBuilder()
                .withServiceName(clientFactory.getServiceName())
                .withId("emodb-us-east-1")
                .withPayload(new PayloadBuilder()
                        .withUrl(uri.resolve(DataStoreClient.SERVICE_PATH))
                        .withAdminUrl(uri)
                        .toString())
                .build();

        return ServicePoolBuilder.create(DataStore.class)
                .withMetricRegistry(metricRegistry)
                .withHostDiscovery(new FixedHostDiscovery(endPoint))
                .withServiceFactory(clientFactory)
                .buildProxy(new ExponentialBackoffRetry(30, 1, 10, TimeUnit.SECONDS));
    }

    /**
     * Provides a eu-west-1 Emo client.
     */
    @Provides
    @Singleton
    @EmoDataStoreEuWest1Client
    DataStore provideEmoEuWestClient(Client jerseyClient, @Named ("AdminKey") String apiKey, MetricRegistry metricRegistry) {

        ServiceFactory<DataStore> clientFactory = DataStoreClientFactory
                .forClusterAndHttpClient(_configuration.getCluster(), jerseyClient)
                .usingCredentials(apiKey);

        URI uri = UriBuilder.fromPath(_configuration.getEmoDataCenterConfiguration().getEuWestUri()).build();

        ServiceEndPoint endPoint = new ServiceEndPointBuilder()
                .withServiceName(clientFactory.getServiceName())
                .withId("emodb-eu-west-1")
                .withPayload(new PayloadBuilder()
                        .withUrl(uri.resolve(DataStoreClient.SERVICE_PATH))
                        .withAdminUrl(uri)
                        .toString())
                .build();

        return ServicePoolBuilder.create(DataStore.class)
                .withMetricRegistry(metricRegistry)
                .withHostDiscovery(new FixedHostDiscovery(endPoint))
                .withServiceFactory(clientFactory)
                .buildProxy(new ExponentialBackoffRetry(30, 1, 10, TimeUnit.SECONDS));
    }
}