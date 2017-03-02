package com.bazaarvoice.piiproxy;

import com.bazaarvoice.piiproxy.Resource.PIIProxyResource1;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiiProxyService extends Application<PiiProxyConfiguration> {

    private Injector _injector;

    private static final Logger _log = LoggerFactory.getLogger(PiiProxyService.class);

    public static void main(String[] args)
            throws Exception {
        new PiiProxyService().run(args);
    }

    @Override
    public String getName() {
        return "pii-proxy";
    }

    @Override
    public void initialize(Bootstrap<PiiProxyConfiguration> bootstrap) {

        // Write Date objects using ISO8601 strings instead of numeric milliseconds-since-1970.
        bootstrap.getObjectMapper().setDateFormat(new ISO8601DateFormat());


        // more to be added later.........
    }

    @Override
    public void run(PiiProxyConfiguration configuration,
                    Environment environment) {
        _injector = Guice.createInjector(Stage.PRODUCTION, new PiiProxyModule(environment, configuration, getName()));

        environment.jersey().register(_injector.getInstance(PIIProxyResource1.class));
    }

}