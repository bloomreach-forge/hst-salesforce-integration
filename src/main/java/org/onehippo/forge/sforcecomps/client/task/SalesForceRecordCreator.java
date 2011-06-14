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
package org.onehippo.forge.sforcecomps.client.task;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.onehippo.forge.sforcecomps.client.rest.SalesForceRestClient;

public class SalesForceRecordCreator {

    private SalesForceRestClient client;
    private String baseResourcePath;
    private boolean createOrUpdate;

    public SalesForceRestClient getClient() {
        return client;
    }

    public void setClient(SalesForceRestClient client) {
        this.client = client;
    }

    public String getBaseResourcePath() {
        return baseResourcePath;
    }

    public void setBaseResourcePath(String baseResourcePath) {
        this.baseResourcePath = baseResourcePath;
    }

    public boolean isCreateOrUpdate() {
        return createOrUpdate;
    }

    public void setCreateOrUpdate(boolean createOrUpdate) {
        this.createOrUpdate = createOrUpdate;
    }

    public String createRecord(String json) throws IOException {
        if (createOrUpdate) {
            return client.createOrUpdateRecord(baseResourcePath, JSONObject.fromObject(json)).toString();
        } else {
            return client.createRecord(baseResourcePath, JSONObject.fromObject(json)).toString();
        }
    }

}
