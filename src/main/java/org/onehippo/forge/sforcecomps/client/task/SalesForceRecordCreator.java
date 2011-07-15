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

import org.onehippo.forge.sforcecomps.client.util.JSONUtils;

public class SalesForceRecordCreator extends AbstractSalesForceTask {

    private boolean createOrUpdate;

    public boolean isCreateOrUpdate() {
        return createOrUpdate;
    }

    public void setCreateOrUpdate(boolean createOrUpdate) {
        this.createOrUpdate = createOrUpdate;
    }

    public String perform(String json) throws IOException {
        client.establishAccessToken();
        
        if (createOrUpdate) {
            return JSONUtils.toString(client.createOrUpdateRecord(baseResourcePath, JSONObject.fromObject(json)));
        } else {
            return JSONUtils.toString(client.createRecord(baseResourcePath, JSONObject.fromObject(json)));
        }
    }

}
