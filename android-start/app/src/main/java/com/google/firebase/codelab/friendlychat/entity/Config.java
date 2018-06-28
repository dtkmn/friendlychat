package com.google.firebase.codelab.friendlychat.entity;

public class Config {

    private final String grantType = "client_credentials";
    private final String scopeValue = "PUSHFCM-MGMT";
    // UAT
//    private final String clientId = "SKoEL7R7kg3GhFO7xGV4Yj39jNWzTxxO";
//    private final String clientSecret = "8NSrfe1lAWUXDjtS";
    // Staging/Prod
    private final String clientId = "DSXI2p3xS63OmS2IfXVSZN49iTRbxlUx";
    private final String clientSecret = "Ttdc5O1bqFS1LUe7";

    // Internal UAT
//    private final String baseUrl = "slot2.org002.t-dev.corp.telstra.com";
    // External UAT
//    private final String baseUrl = "slot2.org002.t-dev.telstra.net";
    // Staging
    private final String baseUrl = "staging-tapi.telstra.com";
    // Prod
//    private final String baseUrl = "private-tapi.telstra.com";


    public String getGrantType() {
        return grantType;
    }

    public String getScopeValue() {
        return scopeValue;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
