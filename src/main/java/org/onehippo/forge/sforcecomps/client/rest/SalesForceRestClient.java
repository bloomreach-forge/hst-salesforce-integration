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

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Simple SalesForce REST API Wrapper using HTTP Client
 * @see http://www.salesforce.com/us/developer/docs/api_rest/index.htm
 */
public class SalesForceRestClient {

    public static final String DEFAULT_BASE_URL = "http://na1.salesforce.com";
    public static final String DEFAULT_BASE_SERVICE_BASE_PATH = "/services/data";
    public static final String DEFAULT_CURRENT_SERVICE_BASE_PATH = "/services/data/v20.0";

    private HttpClient httpClient = null;

    private String baseUrl = DEFAULT_BASE_URL;
    private String baseServiceBasePath = DEFAULT_BASE_SERVICE_BASE_PATH;
    private String baseCurrentServiceBasePath = DEFAULT_CURRENT_SERVICE_BASE_PATH;

    private String oauthToken;

    public SalesForceRestClient() {
        this(null);
    }

    public SalesForceRestClient(String baseUrl) {
        this.baseUrl = StringUtils.defaultIfEmpty(baseUrl, DEFAULT_BASE_URL);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseServiceBasePath() {
        return baseServiceBasePath;
    }

    public void setBaseServiceBasePath(String baseServiceBasePath) {
        this.baseServiceBasePath = baseServiceBasePath;
    }

    public String getBaseCurrentServiceBasePath() {
        return baseCurrentServiceBasePath;
    }

    public void setBaseCurrentServiceBasePath(String baseCurrentServiceBasePath) {
        this.baseCurrentServiceBasePath = baseCurrentServiceBasePath;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
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
    public JSONObject getAvailableVersions() throws IOException {
        return getJSONObjectFromURL(baseUrl + StringUtils.removeEnd(baseServiceBasePath, "/") + "/");
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
    public JSONObject getAvailableResources() throws IOException {
        return getJSONObjectFromURL(getServiceBaseUrl());
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
    public JSONObject getObjects() throws IOException {
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
    public JSONObject getObjectsFromResourcePath(String resourcePath) throws IOException {
        return getJSONObjectFromURL(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/");
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
    public JSONObject createRecord(String resourcePath, JSONObject json) throws IOException {
        HttpPost httpRequest = new HttpPost(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/");

        if (oauthToken != null) {
            httpRequest.addHeader("Authorization", oauthToken);
        }
        
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.setEntity(new StringEntity(json.toString()));
        
        HttpEntity httpEntity = null;

        try {
            if (httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            if (status.getStatusCode() >= 400) {
                throw new IOException("Failure: " + status.toString());
            }

            httpEntity = httpResponse.getEntity();
            return JSONObject.fromObject(EntityUtils.toString(httpEntity));
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
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
    public JSONObject updateRecord(String resourcePath, JSONObject json) throws IOException {
        HttpPut httpRequest = new HttpPut(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/"){
            @Override
            public String getMethod() {
                return "PATCH";
            }  
        };

        if (oauthToken != null) {
            httpRequest.addHeader("Authorization", oauthToken);
        }
        
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.setEntity(new StringEntity(json.toString()));
        
        HttpEntity httpEntity = null;

        try {
            if (httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            if (status.getStatusCode() >= 400) {
                throw new IOException("Failure: " + status.toString());
            }

            httpEntity = httpResponse.getEntity();
            return JSONObject.fromObject(EntityUtils.toString(httpEntity));
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
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
    public JSONObject createOrUpdateRecord(String resourcePath, JSONObject json) throws IOException {
        HttpPut httpRequest = new HttpPut(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/") {
            @Override
            public String getMethod() {
                return "PATCH";
            }  
        };

        if (oauthToken != null) {
            httpRequest.addHeader("Authorization", oauthToken);
        }
        
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.setEntity(new StringEntity(json.toString()));
        
        HttpEntity httpEntity = null;

        try {
            if (httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            if (status.getStatusCode() >= 400) {
                throw new IOException("Failure: " + status.toString());
            }

            httpEntity = httpResponse.getEntity();
            return JSONObject.fromObject(EntityUtils.toString(httpEntity));
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
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
    public void deleteRecord(String resourcePath) throws IOException {
        HttpDelete httpRequest = new HttpDelete(StringUtils.removeEnd(getServiceBaseUrl(), "/") + StringUtils.removeEnd(resourcePath, "/") + "/");

        if (oauthToken != null) {
            httpRequest.addHeader("Authorization", oauthToken);
        }
        
        HttpEntity httpEntity = null;

        try {
            if (httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            if (status.getStatusCode() >= 400) {
                throw new IOException("Failure: " + status.toString());
            }

            httpEntity = httpResponse.getEntity();
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
    }
    
    private JSONObject getJSONObjectFromURL(String url) throws IOException {
        HttpRequestBase httpRequest = new HttpGet(url);

        if (oauthToken != null) {
            httpRequest.addHeader("Authorization", oauthToken);
        }

        HttpEntity httpEntity = null;

        try {
            if (httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            StatusLine status = httpResponse.getStatusLine();

            if (status.getStatusCode() >= 400) {
                throw new IOException("Failure: " + status.toString());
            }

            httpEntity = httpResponse.getEntity();
            return JSONObject.fromObject(EntityUtils.toString(httpEntity));
        } finally {
            if (httpEntity != null) {
                EntityUtils.consume(httpEntity);
            }
        }
    }

    private String getServiceBaseUrl() {
        return baseUrl + baseCurrentServiceBasePath;
    }
    
}
