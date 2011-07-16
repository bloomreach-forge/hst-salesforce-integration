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
package org.onehippo.forge.sforcecomps.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SalesForceException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private String message;
    private JSONArray errorArray;

    public SalesForceException() {
        this((JSON) null);
    }

    public SalesForceException(JSON errors) {
        super();
        this.errorArray = toJSONArray(errors);
        this.message = getFirstErrorMessage(errors);
    }

    public SalesForceException(String message) {
        this(message, (JSON) null);
    }

    public SalesForceException(String message, JSON errors) {
        super(message);
        this.errorArray = toJSONArray(errors);
    }

    public SalesForceException(String message, Throwable cause) {
        this(message, cause, (JSON) null);
    }

    public SalesForceException(String message, Throwable cause, JSON errors) {
        super(message, cause);
        this.errorArray = toJSONArray(errors);
    }

    public SalesForceException(Throwable cause) {
        this(cause, (JSON) null);
    }

    public SalesForceException(Throwable cause, JSON errors) {
        super(cause);
        this.errorArray = toJSONArray(errors);
        this.message = getFirstErrorMessage(errors);
    }

    @Override
    public String getMessage() {
        if (message != null) {
            return message;
        } else {
            return super.getMessage();
        }
    }

    public JSONArray getErrors() {
        return errorArray;
    }
    
    private static JSONArray toJSONArray(JSON json) {
        if (json.isArray()) {
            return (JSONArray) json;
        } else {
            JSONArray arr = new JSONArray();
            arr.add(json);
            return arr;
        }
    }
    
    public static String getFirstErrorMessage(JSON json) {
        return getFirstErrorMessage(json, null);
    }
    
    public static String getFirstErrorMessage(JSON json, String defaultMessage) {
        if (json == null) {
            return defaultMessage;
        }
        
        JSONObject jsonObject = null;
        
        if (!json.isArray()) {
            jsonObject = (JSONObject) json;
        } else {
            JSONArray jsonArray = (JSONArray) json;
            
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                break;
            }
        }
        
        if (jsonObject != null) {
            if (jsonObject.containsKey("error_description")) {
                return jsonObject.getString("error_description");
            } else if (jsonObject.containsKey("message")) {
                return jsonObject.getString("message");
            }
        }
        
        return defaultMessage;
    }

    public static List<String> getErrorMessages(JSON json) {
        if (json == null) {
            return Collections.emptyList();
        }
        
        List<String> errorMessages = new ArrayList<String>();
        
        if (!json.isArray()) {
            JSONObject jsonObject = (JSONObject) json;
            
            if (jsonObject.containsKey("error_description")) {
                errorMessages.add(jsonObject.getString("error_description"));
            } else if (jsonObject.containsKey("message")) {
                errorMessages.add(jsonObject.getString("message"));
            }
        } else {
            JSONArray jsonArray = (JSONArray) json;
            
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                
                if (jsonObject.containsKey("error_description")) {
                    errorMessages.add(jsonObject.getString("error_description"));
                } else if (jsonObject.containsKey("message")) {
                    errorMessages.add(jsonObject.getString("message"));
                }
            }
        }
        
        return errorMessages;
    }
}
