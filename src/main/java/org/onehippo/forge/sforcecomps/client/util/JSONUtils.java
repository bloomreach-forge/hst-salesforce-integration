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
package org.onehippo.forge.sforcecomps.client.util;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

public class JSONUtils {
    
    private JSONUtils() {
        
    }
    
    public static boolean containsId(JSONObject json) {
        if (json.containsKey("id")) {
            return true;
        } else if (json.containsKey("Id")) {
            return true;
        } else if (json.containsKey("ID")) {
            return true;
        }
        
        return false;
    }
    
    public static String getId(JSONObject json) {
        if (json.containsKey("id")) {
            return json.getString("id");
        } else if (json.containsKey("Id")) {
            return json.getString("Id");
        } else if (json.containsKey("ID")) {
            return json.getString("ID");
        }
        
        return null;
    }
    
    public static void removeId(JSONObject json) {
        if (json.containsKey("id")) {
            json.remove("id");
        } else if (json.containsKey("Id")) {
            json.remove("Id");
        } else if (json.containsKey("ID")) {
            json.remove("ID");
        }
    }
    
    public static String toString(JSON json) {
        return toString(json, null);
    }
    
    public static String toString(JSON json, String defaultStr) {
        if (json != null) {
            return json.toString();
        }
        
        return defaultStr;
    }
}
