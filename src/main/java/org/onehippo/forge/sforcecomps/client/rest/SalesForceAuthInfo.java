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

import org.onehippo.forge.sforcecomps.client.util.JSONUtils;

import net.sf.json.JSONObject;


public class SalesForceAuthInfo {
    
    private String id;
    private long issuedAt;
    private String instanceUrl;
    private String signature;
    private String accessToken;
    private long createdAt;
    
    public SalesForceAuthInfo(JSONObject json) {
        this.id = JSONUtils.getId(json);
        this.issuedAt = json.getLong("issued_at");
        this.instanceUrl = json.getString("instance_url");
        this.signature = json.getString("signature");
        this.accessToken = json.getString("access_token");
        this.createdAt = System.currentTimeMillis();
    }
    
    public String getId() {
        return id;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public String getSignature() {
        return signature;
    }

    public String getAccessToken() {
        return accessToken;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
}
