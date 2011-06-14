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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import net.sf.json.JSONObject;

public class SalesForceRestBrowser {
    
    private SalesForceConnectionInfo connectionInfo;
    private SalesForceRestClient client;
    
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
    }
    
    public JSONObject getObjectsFromResourcePath(String resourcePath) throws Exception {
        JSONObject jsonObject = client.getObjectsFromResourcePath(resourcePath);
        return jsonObject;
    }
    
    public boolean checkSalesForcePropertiesConfigured() {
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
    
    public static void main(String [] args) throws Exception {
        SalesForceRestBrowser browser = new SalesForceRestBrowser();
        browser.setUp();
        
        if (!browser.checkSalesForcePropertiesConfigured()) {
            return;
        }
        
        System.console().printf("\n");
        System.console().printf("SalesForce Resource Browser\n");
        System.console().printf(" - Enter a resource path you want to browse. E.g., \"/sobjects/Account\"\n");
        System.console().printf("\n");
        System.console().printf("%s: ", "Enter resource path");
        String line = System.console().readLine().trim();
        
        while (!"quit".equals(line)) {
            if (!"".equals(line)) {
                JSONObject json = browser.getObjectsFromResourcePath(line);
                System.out.println(json);
                System.out.println();
            }
            
            System.console().printf("%s: ", "Enter resource path");
            line = System.console().readLine().trim();
        }
    }
    
}
