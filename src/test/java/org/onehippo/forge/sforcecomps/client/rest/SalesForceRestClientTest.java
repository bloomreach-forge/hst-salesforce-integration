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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesForceRestClientTest {
    
    private static Logger log = LoggerFactory.getLogger(SalesForceRestClientTest.class);
    
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String securityToken;
    
    private SalesForceRestClient client;
    
    @Before
    public void setUp() throws Exception {
        Properties props = new Properties();
        File propFile = new File("build.properties");
        
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
        
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }
        
        client = new SalesForceRestClient();
        client.setClientId(clientId);
        client.setClientSecret(clientSecret);
        client.setUsername(username);
        client.setPassword(password);
        client.setSecurityToken(securityToken);
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

        JSONArray jsonArray = client.getAvailableVersions();
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

        JSONObject jsonObject = client.getAvailableResources();
        log.info("Available Resources: " + jsonObject);
    }
    
    @Test
    public void testGetObjects() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSONObject jsonObject = client.getObjects();
        log.info("Objects: " + jsonObject);
    }
    
    @Test
    public void testGetObjectsFromResourcePath() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSONObject jsonObject = client.getObjectsFromResourcePath("/sobjects/Account");
        log.info("Objects: " + jsonObject);
    }
    
    @Test
    public void testCreateAndUpdateAndDeleteRecord() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        JSONObject json = JSONObject.fromObject("{ \"Name\": \"test\" } ");
        JSONObject jsonObject = client.createRecord("/sobjects/Account", json);
        log.info("Created Objects: " + jsonObject);
        String id = jsonObject.getString("id");
        
        client.updateRecord("/sobjects/Account/" + id, JSONObject.fromObject("{ \"BillingCity\" : \"San Francisco\" }"));
        jsonObject = client.getObjectsFromResourcePath("/sobjects/Account/" + id);
        log.info("Updated Objects: " + jsonObject);
        
        client.createOrUpdateRecord("/sobjects/Account/" + id, JSONObject.fromObject("{ \"BillingCountry\" : \"USA\" }"));
        jsonObject = client.getObjectsFromResourcePath("/sobjects/Account/" + id);
        log.info("Updated Objects: " + jsonObject);
        
        client.deleteRecord("/sobjects/Account/" + id);
        log.info("Deleted Objects: " + jsonObject);
    }
    
    private boolean checkSalesForcePropertiesConfigured() {
        boolean configured = true;
        
        if (StringUtils.isBlank(clientId)) {
            configured = false;
        }
        
        if (StringUtils.isBlank(clientSecret)) {
            configured = false;
        }
        
        if (StringUtils.isBlank(username)) {
            configured = false;
        }
        
        if (StringUtils.isBlank(password)) {
            configured = false;
        }
        
        if (StringUtils.isBlank(securityToken)) {
            configured = false;
        }
        
        if (!configured) {
            System.out.println("!!!!!!! Set SalesForce Authorization Properties in build.properties !!!!!!!");
        }
        
        return configured;
    }
}
