/*
 * Copyright 2011 Hippo
 *
 *   Licensed under the Apache License, Version 2.0 (the  "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS"
 *   BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.onehippo.forge.sforcecomps.client.rest;

public class SalesForceConnectionInfo {

    public static final String DEFAULT_BASE_URL = "https://na1.salesforce.com";
    public static final String DEFAULT_AUTH_TOKEN_PATH = "/services/oauth2/token";
    public static final String DEFAULT_SERVICES_DATA_PATH = "/services/data";
    public static final String DEFAULT_SERVICES_DATA_ENV_PATH = "/services/data/v20.0";

    private String baseUrl = DEFAULT_BASE_URL;
    private String authTokenPath = DEFAULT_AUTH_TOKEN_PATH;
    private String baseServicesDataPath = DEFAULT_SERVICES_DATA_PATH;
    private String baseServicesDataEnvPath = DEFAULT_SERVICES_DATA_ENV_PATH;

    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String securityToken;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthTokenPath() {
        return authTokenPath;
    }

    public void setAuthTokenPath(String authTokenPath) {
        this.authTokenPath = authTokenPath;
    }

    public String getBaseServicesDataPath() {
        return baseServicesDataPath;
    }

    public void setBaseServicesDataPath(String baseServicesDataPath) {
        this.baseServicesDataPath = baseServicesDataPath;
    }

    public String getBaseServicesDataEnvPath() {
        return baseServicesDataEnvPath;
    }

    public void setBaseServicesDataEnvPath(String baseServicesDataEnvPath) {
        this.baseServicesDataEnvPath = baseServicesDataEnvPath;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

}
