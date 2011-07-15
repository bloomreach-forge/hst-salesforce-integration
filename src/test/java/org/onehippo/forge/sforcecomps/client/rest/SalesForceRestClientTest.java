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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onehippo.forge.sforcecomps.client.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesForceRestClientTest {
    
    private static Logger log = LoggerFactory.getLogger(SalesForceRestClientTest.class);
    
    private SalesForceConnectionInfo connectionInfo;
    private SalesForceRestClient client;
    
    @Before
    public void setUp() throws Exception {
        Properties props = new Properties();
        File propFile = new File("build.properties");
        
        String clientId = null;
        String clientSecret = null;
        String username = null;
        String password = null;
        String securityToken = null;
        
        if (propFile.isFile()) {
            FileInputStream fis = null;
            
            try {
                fis = new FileInputStream(propFile);
                props.load(fis);
            } finally {
                fis.close();
            }
            
            clientId = props.getProperty("salesforce.clientId");
            clientSecret = props.getProperty("salesforce.clientSecret");
            username = props.getProperty("salesforce.username");
            password = props.getProperty("salesforce.password");
            securityToken = props.getProperty("salesforce.securityToken");
        }
        
        connectionInfo = new SalesForceConnectionInfo();
        connectionInfo.setClientId(clientId);
        connectionInfo.setClientSecret(clientSecret);
        connectionInfo.setUsername(username);
        connectionInfo.setPassword(password);
        connectionInfo.setSecurityToken(securityToken);

        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }
        
        client = new SalesForceRestClient();
        client.setConnectionInfo(connectionInfo);
        client.establishAccessToken();
        
        assertFalse(StringUtils.isBlank(client.getAuthInfo().getAccessToken()));
        assertTrue(client.getAuthInfo().getIssuedAt() > 0L);
    }
    
    @After
    public void tearDown() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }
    }

    @Test
    public void testGetAvailableVersions() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSONArray jsonArray = (JSONArray) client.getAvailableVersions();
        log.info("Available Versions: " + jsonArray);
        assertTrue(jsonArray.size() > 0);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        assertNotNull(jsonObject);
    }
    
    @Test
    public void testGetAvailableResources() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSON json = client.getAvailableResources();
        log.info("Available Resources: " + json);
    }
    
    @Test
    public void testGetObjects() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSON json = client.getObjects();
        log.info("Objects: " + json);
    }
    
    @Test
    public void testGetObjectsFromResourcePath() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSON json = client.getObjectsFromResourcePath("/sobjects/Account");
        log.info("Objects: " + json);
    }
    
    @Test
    public void testCreateAndUpdateAndDeleteRecord() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSONObject json = JSONObject.fromObject("{ \"Name\": \"test\" } ");
        JSONObject jsonRet = (JSONObject) client.createRecord("/sobjects/Account", json);
        log.info("Created Objects: " + jsonRet);
        String id = JSONUtils.getId(jsonRet);
        
        client.updateRecord("/sobjects/Account/" + id + "/", JSONObject.fromObject("{ \"BillingCity\" : \"San Francisco\" }"));
        jsonRet = (JSONObject) client.getObjectsFromResourcePath("/sobjects/Account/" + id);
        log.info("Updated Objects: " + jsonRet);
        
        client.createOrUpdateRecord("/sobjects/Account/" + id + "/", JSONObject.fromObject("{ \"BillingCountry\" : \"USA\" }"));
        jsonRet = (JSONObject) client.getObjectsFromResourcePath("/sobjects/Account/" + id);
        log.info("Updated Objects: " + jsonRet);
        
        client.deleteRecord("/sobjects/Account/" + id + "/");
        log.info("Deleted Objects: " + jsonRet);
    }
    
    private boolean checkSalesForcePropertiesConfigured() {
        if (connectionInfo == null) {
            System.out.println("!!!!!!! Set SalesForce Authorization Properties in build.properties !!!!!!!");
            return false;
        }
        
        if (connectionInfo.getClientId() == null) {
            System.out.println("!!!!!!! Set SalesForce Authorization Properties in build.properties !!!!!!!");
            return false;
        }
        
        if (connectionInfo.getClientSecret() == null) {
            System.out.println("!!!!!!! Set SalesForce Authorization Properties in build.properties !!!!!!!");
            return false;
        }
        
        if (connectionInfo.getUsername() == null) {
            System.out.println("!!!!!!! Set SalesForce Authorization Properties in build.properties !!!!!!!");
            return false;
        }
        
        if (connectionInfo.getPassword() == null) {
            System.out.println("!!!!!!! Set SalesForce Authorization Properties in build.properties !!!!!!!");
            return false;
        }
        
        if (connectionInfo.getSecurityToken() == null) {
            System.out.println("!!!!!!! Set SalesForce Authorization Properties in build.properties !!!!!!!");
            return false;
        }
        
        return true;
    }
}
