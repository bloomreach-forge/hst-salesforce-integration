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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.onehippo.forge.sforcecomps.client.SalesForceException;
import org.onehippo.forge.sforcecomps.client.SalesForceRecordCreateException;
import org.onehippo.forge.sforcecomps.client.SalesForceRecordDeleteException;
import org.onehippo.forge.sforcecomps.client.SalesForceRecordQueryException;
import org.onehippo.forge.sforcecomps.client.SalesForceRecordUpdateException;
import org.onehippo.forge.sforcecomps.client.SalesForceSecurityException;
import org.onehippo.forge.sforcecomps.client.util.JSONUtils;

/**
 * Simple SalesForce REST API Wrapper using HTTP Client
 * @see http://www.salesforce.com/us/developer/docs/api_rest/index.htm
 */
public class SalesForceRestClient {
    
    private HttpClient httpClient = null;
    private boolean autoEstablishAuthToken = true;
    private long authTokenTimeout = 30L * 60L * 1000L;
    private SalesForceConnectionInfo connectionInfo = new SalesForceConnectionInfo();    
    private SalesForceAuthInfo authInfo;
    
    public boolean isAutoEstablishAuthToken() {
        return autoEstablishAuthToken;
    }

    public void setAutoEstablishAuthToken(boolean autoEstablishAuthToken) {
        this.autoEstablishAuthToken = autoEstablishAuthToken;
    }
    
    public long getAuthTokenTimeout() {
        return authTokenTimeout;
    }

    public void setAuthTokenTimeout(long authTokenTimeout) {
        this.authTokenTimeout = authTokenTimeout;
    }

    public void setConnectionInfo(SalesForceConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public SalesForceConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }
    
    public SalesForceAuthInfo getAuthInfo() {
        return authInfo;
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }
        
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    public void establishAccessToken() throws SalesForceException, IOException {
        if (autoEstablishAuthToken && authInfo != null) {
            if (authTokenTimeout <= 0) {
                return;
            } else if (System.currentTimeMillis() - authInfo.getCreatedAt() < authTokenTimeout) {
                return;
            }
        }
        
        List <NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.add(new BasicNameValuePair("grant_type", "password"));
        formParams.add(new BasicNameValuePair("client_id", getConnectionInfo().getClientId()));
        formParams.add(new BasicNameValuePair("client_secret", getConnectionInfo().getClientSecret()));
        formParams.add(new BasicNameValuePair("username", getConnectionInfo().getUsername()));
        formParams.add(new BasicNameValuePair("password", getConnectionInfo().getPassword() + getConnectionInfo().getSecurityToken()));
        HttpPost post = new HttpPost(getConnectionInfo().getBaseUrl() + getConnectionInfo().getAuthTokenPath());
        post.setEntity(new UrlEncodedFormEntity(formParams));

        HttpEntity httpEntity = null;

        try {
            HttpResponse httpResponse = getHttpClient().execute(post);
            httpEntity = httpResponse.getEntity();
            
            if (httpEntity != null) {
                JSON json = JSONSerializer.toJSON(EntityUtils.toString(httpEntity));
                JSONObject jsonRet = (json.isArray() ? ((JSONArray) json).getJSONObject(0) : (JSONObject) json);
                
                if (!JSONUtils.containsId(jsonRet)) {
                    throw new SalesForceSecurityException("Failed to establish auth token. " + SalesForceException.getFirstErrorMessage(json, ""), json);
                }
                
                authInfo = new SalesForceAuthInfo(jsonRet);
            }
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
    }

    /**
     * Lists summary information about each REST API version currently available, including the version, label, and a link to each version's root. 
     * You do not need authentication to retrieve the list of versions.
     * <H3>Example usage</H3>
     * <PRE>curl http://na1.salesforce.com/services/data/</PRE>
     * <H3>Example request body</H3>
     * <PRE>none required</PRE>
     * <H3>Example response body</H3>
     * <PRE>
     * [
     *     {
     *         "label":"Winter '10"
     *         "version":"20.0",
     *         "url":"/services/data/v20.0",
     *     }
     * ]
     * </PRE>
     * @return
     * @throws IOException
     * @see http://www.salesforce.com/us/developer/docs/api_rest/index.htm
     */
    public JSON getAvailableVersions() throws IOException {
        return getJSONFromURL(getConnectionInfo().getBaseUrl() + StringUtils.removeEnd(getConnectionInfo().getBaseServicesDataPath(), "/") + "/");
    }

    /**
     * List the resources available for the specified API version. It provides the name and URI of each resource.
     * Example
     * curl https://na1.salesforce.com/services/data/v20.0/ -H "Authorization: OAuth token" -H "X-PrettyPrint:1"
     *                         
     * Example request body
     * none required
     * Example response body
     * {
     *     "sobjects" : "/services/data/v20.0/sobjects",
     *     "search" : "/services/data/v20.0/search",
     *     "query" : "/services/data/v20.0/query",
     *     "recent" : "/services/data/v20.0/recent"
     * }
     * @return
     * @throws IOException
     * @see http://www.salesforce.com/us/developer/docs/api_rest/index.htm
     */
    public JSON getAvailableResources() throws IOException {
        return getJSONFromURL(getServiceBaseUrl());
    }

    /**
     * List the objects that are available in your organization and available to the logged-in user. 
     * This request also returns the organization encoding as well as maximum batch size permitted in queries.
     * <H3>Example usage</H3>
     * <PRE>curl https://na1.salesforce.com/services/data/v20.0/sobjects/ -H "Authorization: OAuth token" -H "X-PrettyPrint:1"</PRE>
     *                         
     * <H3>Example request body</H3>
     * <PRE>none required</PRE>
     * <H3>Example response body</H3>
     * <PRE>
     * Date: Thu, 21 Oct 2010 22:48:18 GMT 
     * Transfer-Encoding: chunked 
     * Content-Type: application/json; 
     * charset=UTF-8 Server:
     * {
     * "encoding" : "UTF-8",
     * "maxBatchSize" : 200,
     *  "sobjects" : [ {
     *  "name" : "Account",
     *  "label" : "Account",
     *  "custom" : false,
     *  "keyPrefix" : "001",
     *  "updateable" : true,
     *  "searchable" : true,
     *  "labelPlural" : "Accounts",
     *  "layoutable" : true,
     *  "activateable" : false,
     *  "urls" : { "sobject" : "/services/data/v20.0/sobjects/Account",
     *  "describe" : "/services/data/v20.0/sobjects/Account/describe",
     *  "rowTemplate" : "/services/data/v20.0/sobjects/Account/{ID}" },
     *  "createable" : true,
     *  "customSetting" : false,
     *  "deletable" : true,
     *  "deprecatedAndHidden" : false,
     *  "feedEnabled" : false,
     *  "mergeable" : true,
     *  "queryable" : true,
     *  "replicateable" : true,
     *  "retrieveable" : true,
     *  "undeletable" : true,
     *  "triggerable" : true },
     * },
     * ...
     * </PRE> 
     * @see http://www.salesforce.com/us/developer/docs/api_rest/index.htm
     */
    public JSON getObjects() throws IOException {
        return getObjectsFromResourcePath("/sobjects/");
    }

    /**
     * Generic query API from a resource path such as:
     * <PRE>
     * /sobjects/
     * /sobjects/Account/
     * /sobjects/Account/describe/
     * /query/?q=SELECT+name+from+Account
     * </PRE>
     * @param resourcePath
     * @return
     * @throws IOException
     * @see http://www.salesforce.com/us/developer/docs/api_rest/index.htm
     */
    public JSON getObjectsFromResourcePath(String resourcePath) throws IOException {
        return getJSONFromURL(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/"));
    }
    
    /**
     * Use the HTTP POST method to create a new Account record.
     * <H3>Example cURL command</H3>
     * <PRE>curl https://na1.salesforce.com/services/data/v20.0/sobjects/Account/ -H "Content-Type: application/json" -d @newaccount.json -H "Authorization: OAuth token" -H "X-PrettyPrint:1"</PRE>
     *                             
     * <H3>Example request body for posting a new Account (contents of newaccount.json)</H3>
     * <PRE>
     * {
     *     "Name" : "test"
     * }
     * </PRE>
     * <H3>Example response body for posting a new Account</H3>
     * <PRE>
     * {
     *     "id" : "001D000000IqhSLIAZ",
     *     "errors" : [ ],
     *     "success" : true
     * }
     * </PRE>
     */    
    public JSON createRecord(String resourcePath, JSON json) throws SalesForceException, IOException {
        if (autoEstablishAuthToken) {
            establishAccessToken();
        }
        
        JSON jsonRet = null;
        HttpPost httpRequest = new HttpPost(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/");
        addHeaders(httpRequest);
        httpRequest.setEntity(new StringEntity(JSONUtils.toString(json, "")));
        
        HttpEntity httpEntity = null;

        try {
            HttpResponse httpResponse = getHttpClient().execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            httpEntity = httpResponse.getEntity();
            
            if (httpEntity != null) {
                jsonRet = JSONSerializer.toJSON(EntityUtils.toString(httpEntity));
            }
            
            if (status.getStatusCode() >= 400) {
                throw new SalesForceRecordCreateException("Fail to create record on " + StringUtils.substringAfter(resourcePath, "/sobjects/") + ". " + SalesForceException.getFirstErrorMessage(jsonRet, ""), jsonRet);
            }
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
        
        return jsonRet;
    }
    
    /**
     * Update a record using HTTP PATCH. First, create a file that contains the changes, then specify it in the cURL command.Records in a single file must be of the same object type.
     * <H3>Example for updating two fields in an Account object</H3>
     * <PRE>curl https://na1.salesforce.com/services/data/v20.0/sobjects/Account/001D000000INjVe  -H "Authorization: OAuth token" -H "X-PrettyPrint:1" -H "Content-Type: application/json" -d @patchaccount.json -X PATCH</PRE>
     *                         
     * <H3>Example request body for updating fields in an Account object</H3>
     * <PRE>
     * {
     *     "BillingCity" : "San Francisco"
     * }
     * </PRE>
     * <H3>Example response body for updating fields in an Account object</H3>
     * <PRE>
     * none returned
     * </PRE>
     */
    public JSON updateRecord(String resourcePath, JSON json) throws SalesForceException, IOException {
        if (autoEstablishAuthToken) {
            establishAccessToken();
        }
        
        JSON jsonRet = null;
        HttpPut httpRequest = new HttpPut(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/"){
            @Override
            public String getMethod() {
                return "PATCH";
            }  
        };

        addHeaders(httpRequest);
        httpRequest.setEntity(new StringEntity(json.toString()));
        
        HttpEntity httpEntity = null;

        try {
            HttpResponse httpResponse = getHttpClient().execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            httpEntity = httpResponse.getEntity();
            
            if (httpEntity != null) {
                jsonRet = JSONSerializer.toJSON(EntityUtils.toString(httpEntity));
            }
            
            if (status.getStatusCode() >= 400) {
                throw new SalesForceRecordUpdateException("Fail to update record on " + StringUtils.substringAfter(resourcePath, "/sobjects/") + ". " + SalesForceException.getFirstErrorMessage(jsonRet, ""), jsonRet);
            }
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
        
        return jsonRet;
    }
    
    /**
     * Create new records or update existing records (upsert) based on the value of a specified external ID field.
     * If the specified value doesn't exist, a new record is created.
     * If a record does exist with that value, the field values specified in the request body are updated.
     * If the value is not unique, the REST API returns a 300 response with the list of matching records.
     * The following sections show how to work with the extneral ID resource to retrieve records by external ID and upsert records, using either JSON or XML.
     * Upserting Existing Records with JSON
     * Use the PATCH method to update individual records, or create them if they don't already exist, based on the external ID field, for an object. In this example, the Billing City on an Account record is updated.
     * <H3>Example: upserting a record that already exists</H3>
     * <PRE>curl https://na1.salesforce.com/services/data/v20.0/sobjects/Account/accountMaster__c/ABC123 -v —H "Authorization:
     * OAuth token" -H "Content-Type: application/json" -X PATCH –d @fileName</PRE>
     *                             
     * <H3>JSON Example request body for updating the BillingCity field</H3>
     * <PRE>
     * Request body:
     * {
     * 
     * BillingCity : "San Francisco"
     * 
     * }
     * </PRE>
     * BillingCity is a field on the record, with the new value to be updated or inserted.
     * <H3>JSON Example Response</H3>
     * HTTP status code 204 is returned if an existing record is updated.
     * <H3>Error responses</H3>
     * If the external ID value isn't unique, an HTTP status code 300 is returned, plus a list of the records that matched the query. For more information about errors, see Error Response.
     * If the external ID field doesn't exist, an error message and code is returned:
     * <PRE>
     * {
     *   "message" : "The requested resource does not exist",
     *   "errorCode" : "NOT_FOUND"
     * }
     * </PRE>
     */
    public JSON createOrUpdateRecord(String resourcePath, JSON json) throws SalesForceException, IOException {
        if (autoEstablishAuthToken) {
            establishAccessToken();
        }
        
        JSON jsonRet = null;
        HttpPut httpRequest = new HttpPut(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/") {
            @Override
            public String getMethod() {
                return "PATCH";
            }  
        };

        addHeaders(httpRequest);
        httpRequest.setEntity(new StringEntity(json.toString()));
        
        HttpEntity httpEntity = null;

        try {
            HttpResponse httpResponse = getHttpClient().execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            httpEntity = httpResponse.getEntity();
            
            if (httpEntity != null) {
                jsonRet = JSONSerializer.toJSON(EntityUtils.toString(httpEntity));
            }
            
            if (status.getStatusCode() >= 400) {
                throw new SalesForceRecordUpdateException("Fail to createOrUpdate record on " + StringUtils.substringAfter(resourcePath, "/sobjects/") + ". " + SalesForceException.getFirstErrorMessage(jsonRet, ""), jsonRet);
            }
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
        
        return jsonRet;
    }
    
    /**
     * Delete a record using HTTP DELETE.
     * Use the DELETE method to delete an object.
     * <H3>Example for deleting an Account record</H3>
     * <PRE>curl https://na1.salesforce.com/services/data/v20.0/sobjects/Account/001D000000INjVe  -H "Authorization: OAuth token" -H "X-PrettyPrint:1" -H "Authorization: OAuth token" -H "X-PrettyPrint:1" -X DELETE</PRE>
     *                         
     * <H3>Example request body</H3>
     * None needed
     * <H3>Example response body</H3>
     * None returned
     */
    public JSON deleteRecord(String resourcePath) throws SalesForceException, IOException {
        if (autoEstablishAuthToken) {
            establishAccessToken();
        }
        
        JSON jsonRet = null;
        HttpDelete httpRequest = new HttpDelete(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/");

        addHeaders(httpRequest);
        
        HttpEntity httpEntity = null;

        try {
            HttpResponse httpResponse = getHttpClient().execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            httpEntity = httpResponse.getEntity();
            
            if (httpEntity != null) {
                jsonRet = JSONSerializer.toJSON(EntityUtils.toString(httpEntity));
            }
            
            if (status.getStatusCode() >= 400) {
                throw new SalesForceRecordDeleteException("Fail to delete record from " + StringUtils.substringAfter(resourcePath, "/sobjects/") + ". " + SalesForceException.getFirstErrorMessage(jsonRet, ""), jsonRet);
            }
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
        
        return jsonRet;
    }
    
    private JSON getJSONFromURL(String url) throws IOException {
        return JSONSerializer.toJSON(getJSONStringFromURL(url));
    }
    
    private String getJSONStringFromURL(String url) throws SalesForceException, IOException {
        if (autoEstablishAuthToken) {
            establishAccessToken();
        }
        
        String jsonStr = null;
        HttpRequestBase httpRequest = new HttpGet(url);

        addHeaders(httpRequest);

        HttpEntity httpEntity = null;

        try {
            HttpResponse httpResponse = getHttpClient().execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            httpEntity = httpResponse.getEntity();
            
            if (httpEntity != null) {
                jsonStr = EntityUtils.toString(httpEntity);
            }
            
            if (status.getStatusCode() >= 400) {
                String resourcePath = null;
                
                if (StringUtils.contains(url, "/query/")) {
                    resourcePath = StringUtils.substringAfter(url, "/query/");
                } else {
                    resourcePath = StringUtils.substringAfter(url, "/sobjects/");
                }
                
                JSON jsonRet = JSONSerializer.toJSON(jsonStr);
                throw new SalesForceRecordQueryException("Failed to retrieve record(s) from " + resourcePath + ". " + SalesForceException.getFirstErrorMessage(jsonRet, ""), jsonRet);
            }
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
        
        return jsonStr;
    }

    private String getServiceBaseUrl() {
        if (authInfo.getInstanceUrl() != null) {
            return authInfo.getInstanceUrl() + getConnectionInfo().getBaseServicesDataEnvPath();
        } else {
            return getConnectionInfo().getBaseUrl() + getConnectionInfo().getBaseServicesDataEnvPath();
        }
    }
    
    private void addHeaders(HttpRequestBase httpRequest) {
        if (authInfo.getAccessToken() != null) {
            httpRequest.addHeader("Authorization", "OAuth " + authInfo.getAccessToken());
        }
        
        httpRequest.addHeader("Content-Type", "application/json");
    }
    
}
