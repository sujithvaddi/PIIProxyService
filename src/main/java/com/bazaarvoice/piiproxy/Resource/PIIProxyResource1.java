package com.bazaarvoice.piiproxy.Resource;

import com.bazaarvoice.emodb.common.uuid.TimeUUIDs;
import com.bazaarvoice.emodb.sor.delta.Delta;
import com.bazaarvoice.emodb.sor.delta.Deltas;
import com.bazaarvoice.piiproxy.pii.PiiProxySource;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Resource containing all the PII Proxy APIs
 */
@Path ("/pii/1")
@Produces (MediaType.APPLICATION_JSON)
public class PIIProxyResource1 {

    private static final Logger _log = LoggerFactory.getLogger(PIIProxyResource1.class);

    private final PiiProxySource _piiProxySource;

    @Inject
    public PIIProxyResource1(PiiProxySource piiProxySource) {
        _piiProxySource = piiProxySource;
    }

    /**
     * creates a table.
     */
    @PUT
    @Path ("_table/{table}")
    @Timed (name = "bv.piiproxy.PIIProxyResource1.createTable", absolute = true)
    public Response createTable(@PathParam ("table") final String table,
                                @QueryParam ("audit") final String audit,
                                @QueryParam ("apiKey") final String apiKey) {
        checkArgument(!Strings.isNullOrEmpty(audit), "audit is required");
        checkArgument(!Strings.isNullOrEmpty(apiKey), "apiKey is required");

        _piiProxySource.createTable(table, audit, apiKey);

        return Response.status(Response.Status.OK).build();
    }

    /**
     * creates a piece of pii content.
     */
    @POST
    @Path ("{table}/")
    @Consumes (MediaType.APPLICATION_JSON)
    @Timed (name = "bv.piiproxy.PIIProxyResource1.update", absolute = true)
    public Response create(@PathParam ("table") final String table,
                           @QueryParam ("locale") final String locale,
                           @QueryParam ("audit") final String audit,
                           @QueryParam ("apiKey") final String apiKey,
                           Map<String, Object> json) {
        checkArgument(!Strings.isNullOrEmpty(locale), "locale is required");
        checkArgument(!Strings.isNullOrEmpty(audit), "audit is required");
        checkArgument(!Strings.isNullOrEmpty(apiKey), "apiKey is required");
        checkArgument(json.isEmpty(), "json is required");

        _piiProxySource.updateContent(table, null, json, locale, audit, apiKey);

        return Response.status(Response.Status.OK).build();
    }

    /**
     * modifies a piece of pii content.
     */
    @POST
    @Path ("{table}/{key}")
    @Consumes (MediaType.APPLICATION_JSON)
    @Timed (name = "bv.piiproxy.PIIProxyResource1.update", absolute = true)
    public Response update(@PathParam ("table") final String table,
                           @QueryParam ("key") final String key,
                           @QueryParam ("locale") final String locale,
                           @QueryParam ("audit") final String audit,
                           @QueryParam ("apiKey") final String apiKey,
                           Map<String, Object> json) {
        checkArgument(!Strings.isNullOrEmpty(locale), "locale is required");
        checkArgument(!Strings.isNullOrEmpty(audit), "audit is required");
        checkArgument(!Strings.isNullOrEmpty(apiKey), "apiKey is required");
        checkArgument(json.isEmpty(), "json is required");

        _piiProxySource.updateContent(table, key, json, locale, audit, apiKey);

        return Response.status(Response.Status.OK).build();
    }

    /**
     * deletes a piece of pii content.
     */
    @DELETE
    @Path ("{table}/{key}")
    @Timed (name = "bv.piiproxy.PIIProxyResource1.delete", absolute = true)
    public Response delete(@PathParam ("table") final String table,
                           @PathParam ("key") final String key,
                           @QueryParam ("locale") final String locale,
                           @QueryParam ("audit") final String audit,
                           @QueryParam ("apiKey") final String apiKey,
                           Map<String, Object> json) {
        checkArgument(!Strings.isNullOrEmpty(locale), "locale is required");
        checkArgument(!Strings.isNullOrEmpty(audit), "audit is required");
        checkArgument(!Strings.isNullOrEmpty(apiKey), "apiKey is required");

        _piiProxySource.updateContent(table, key, json, locale, audit, apiKey);

        return Response.status(Response.Status.OK).build();
    }

    /**
     * Retrieves the current version of the key.
     */
    @GET
    @Path ("{table}/{key}")
    @Timed (name = "bv.piiproxy.PIIProxyResource1.get", absolute = true)
    public Map<String, Object> get(@PathParam ("table") final String table,
                                   @PathParam ("key") final String key,
                                   @QueryParam ("apiKey") final String apiKey) {
        checkArgument(!Strings.isNullOrEmpty(apiKey), "apiKey is required");

        return _piiProxySource.getContent(table, key, apiKey);

//        return Response.ok(...).build();
    }
}