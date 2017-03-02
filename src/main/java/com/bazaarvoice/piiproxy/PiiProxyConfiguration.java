package com.bazaarvoice.piiproxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import java.util.List;

public class PiiProxyConfiguration extends Configuration {

    private String _cluster;

    private EmoDataCenterConfiguration _emoDataCenterConfiguration;

    private List<String> _validTablePlacements;

    // More to come .......

    public PiiProxyConfiguration(@JsonProperty ("cluster") String cluster,
                                 @JsonProperty ("emoDataCenterConfiguration") EmoDataCenterConfiguration emoDataCenterConfiguration,
                                 @JsonProperty ("validTablePlacements") List<String> validTablePlacements) {
        _cluster = cluster;
        _emoDataCenterConfiguration = emoDataCenterConfiguration;
        _validTablePlacements = validTablePlacements;
    }

    public String getCluster() {
        return _cluster;
    }

    public EmoDataCenterConfiguration getEmoDataCenterConfiguration() {
        return _emoDataCenterConfiguration;
    }

    public PiiProxyConfiguration setCluster(String cluster) {
        _cluster = cluster;
        return this;
    }

    public PiiProxyConfiguration setEmoDataCenterConfiguration(EmoDataCenterConfiguration emoDataCenterConfiguration) {
        _emoDataCenterConfiguration = emoDataCenterConfiguration;
        return this;
    }

    public static class EmoDataCenterConfiguration {

        // TODO: Change it according to the changed YAML.

        private String usEastUri;
        private String euWestUri;

        public EmoDataCenterConfiguration(@JsonProperty ("usEast1Uri") String usEastUri,
                                          @JsonProperty ("euWest1Uri") String euWestUri) {
            this.usEastUri = usEastUri;
            this.euWestUri = euWestUri;
        }

        public String getUsEastUri() {
            return usEastUri;
        }

        public String getEuWestUri() {
            return euWestUri;
        }
    }

    public static class piiConfiguration {
        // .........
    }

    // ......
}