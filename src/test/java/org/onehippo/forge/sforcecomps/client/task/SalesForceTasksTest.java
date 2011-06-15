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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onehippo.forge.sforcecomps.client.rest.SalesForceConnectionInfo;
import org.onehippo.forge.sforcecomps.client.rest.SalesForceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesForceTasksTest {
    
    private static Logger log = LoggerFactory.getLogger(SalesForceTasksTest.class);
    
    private SalesForceConnectionInfo connectionInfo;
    private SalesForceRestClient client;
    
    private SalesForceRecordRetriever retriever;
    private SalesForceRecordCreator creator;
    private SalesForceRecordUpdater updater;
    private SalesForceRecordDeleter deleter;    
    
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
        
        retriever = new SalesForceRecordRetriever();
        retriever.setClient(client);
        retriever.setBaseResourcePath("/sobjects/Account");
        creator = new SalesForceRecordCreator();
        creator.setClient(client);
        creator.setBaseResourcePath("/sobjects/Account");
        updater = new SalesForceRecordUpdater();
        updater.setClient(client);
        updater.setBaseResourcePath("/sobjects/Account");
        deleter = new SalesForceRecordDeleter();
        deleter.setClient(client);
        deleter.setBaseResourcePath("/sobjects/Account");
    }
    
    @After
    public void tearDown() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }
    }
    
    @Test
    public void testTasks() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }
        
        String json = "{ \"Name\": \"test\" }";
        String ret = creator.perform(json);
        log.info("Created: " + ret);
        JSONObject jsonRet = JSONObject.fromObject(ret);
        String id = jsonRet.getString("id");
        json = "{ \"id\": \"" + id + "\", \"BillingCity\" : \"San Francisco\" }";
        updater.perform(json);
        ret = retriever.perform("/" + id);
        log.info("Updated: " + ret);
        json = "{ \"id\": \"" + id + "\" }";
        deleter.perform(json);
    }
    
    @Test
    public void testSOQL() throws Exception {
        if (!checkSalesForcePropertiesConfigured()) {
            return;
        }

        retriever.setBaseResourcePath(null);
        String ret = retriever.perform("/query/?q=SELECT+name+from+Account");
        log.info("Query result: " + ret);
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
