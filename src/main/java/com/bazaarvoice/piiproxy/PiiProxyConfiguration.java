package com.bazaarvoice.piiproxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class PiiProxyConfiguration extends Configuration {

    @Valid
    @NotNull
    private String _cluster;

    @Valid
    @NotNull
    private String _systemDatacenter;

    @Valid
    @NotNull
    private String _usEast1Datacenter;

    @Valid
    @NotNull
    private String _euWest1Datacenter;

    @Valid
    @NotNull
    private Set<EmoDataCenterConfiguration> _emoDataCenterConfiguration;

    @Valid
    @NotNull
    private String _adminApiKey;

    private JerseyClientConfiguration _httpClientConfiguration = new JerseyClientConfiguration();

    // More to come .......

    public PiiProxyConfiguration(@JsonProperty ("cluster") String cluster,
                                 @JsonProperty ("systemDataCenter") String systemDatacenter,
                                 @JsonProperty ("usEast1DataCenter") String usEast1Datacenter,
                                 @JsonProperty ("euWest1DataCenter") String euWest1Datacenter,
                                 @JsonProperty ("emoDataCenterConfiguration") Set<EmoDataCenterConfiguration> emoDataCenterConfiguration,
                                 @JsonProperty ("adminApiKey") String adminApiKey,
                                 @JsonProperty ("httpClientConfiguration") JerseyClientConfiguration _jerseyClientConfiguration) {

        _cluster = cluster;
        _systemDatacenter = systemDatacenter;
        _usEast1Datacenter = usEast1Datacenter;
        _euWest1Datacenter = euWest1Datacenter;
        _emoDataCenterConfiguration = emoDataCenterConfiguration;
        _adminApiKey = adminApiKey;

        // TODO: validate the config file - validate that we only have 2 datacenter configs.
    }

    public String getCluster() {
        return _cluster;
    }

    public String getSystemDatacenter() {
        return _systemDatacenter;
    }

    public String getUsEast1Datacenter() {
        return _usEast1Datacenter;
    }

    public String getEuWest1Datacenter() {
        return _euWest1Datacenter;
    }

    public Set<EmoDataCenterConfiguration> getEmoDataCenterConfiguration() {
        return _emoDataCenterConfiguration;
    }

    public String getAdminApiKey() {
        return _adminApiKey;
    }

    public JerseyClientConfiguration getHttpClientConfiguration() {
        return _httpClientConfiguration;
    }

    public static class EmoDataCenterConfiguration {

        private String _datacenter;
        private String _uri;
        private String _placement;
        private Set<String> _locales;

        public EmoDataCenterConfiguration(@JsonProperty ("dataCenter") String datacenter,
                                          @JsonProperty ("uri") String uri,
                                          @JsonProperty ("placement") String placement,
                                          @JsonProperty ("locales") Set<String> locales) {
            _datacenter = datacenter;
            _uri = uri;
            _placement = placement;
            _locales = locales;
        }

        public String getDatacenter() {
            return _datacenter;
        }

        public String getUri() {
            return _uri;
        }

        public String getPlacement() {
            return _placement;
        }

        public Set<String> getLocales() {
            return _locales;
        }
    }
}