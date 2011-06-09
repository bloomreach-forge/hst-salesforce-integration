package com.onehippo.salesforce;

//import org.hippoecm.hst.component.support.forms.BaseFormHstComponent;
import org.hippoecm.hst.core.component.GenericHstComponent;

//import com.teamlazerbeez.crm.sf.core.SObject;
//import com.teamlazerbeez.crm.sf.rest.RestConnection;
//import com.teamlazerbeez.crm.sf.rest.RestConnectionPool;
//import com.teamlazerbeez.crm.sf.rest.RestConnectionPoolImpl;
//import com.teamlazerbeez.crm.sf.rest.RestSObjectImpl;
//import com.teamlazerbeez.crm.sf.rest.SaveResult;
//import com.teamlazerbeez.crm.sf.soap.ApiException;
//import com.teamlazerbeez.crm.sf.soap.BindingConfig;
//import com.teamlazerbeez.crm.sf.soap.ConnectionPool;
//import com.teamlazerbeez.crm.sf.soap.ConnectionPoolImpl;

public class BaseSalesforceFormHstComponent extends GenericHstComponent {
    
    private static final String SALESFORCE_USERNAME = "salesforce.username";
    private static final String SALESFORCE_PASSWORD = "salesforce.password";

//    private ConnectionPool<Integer> soapPool;
//    private RestConnectionPool<Integer> restPool;
    
    
//    protected String getSalesforceUsername(HstRequest request) {
//        return getParameter(SALESFORCE_USERNAME, request);
//    }
//    
//    protected String getSalesforcePassword(HstRequest request) {
//        return getParameter(SALESFORCE_PASSWORD, request);
//    }

//    protected RestConnection getSalesforceRestConnection(String username, String password) {
//        
//        Integer orgId = new Integer(1);
//        String partnerKey = "hst-salesforce";
//        
//        soapPool = new ConnectionPoolImpl<Integer>(partnerKey);
//        soapPool.configureOrg(orgId, username, password, 20);
//
//        restPool = new RestConnectionPoolImpl<Integer>();
//
//        BindingConfig bindingConfig;
//        try {
//            // connect through SOAP API to get a sessionId
//            bindingConfig = soapPool.getConnectionBundle(orgId).getBindingConfig();
//            String host = new URL(bindingConfig.getPartnerServerUrl()).getHost();
//            String token = bindingConfig.getSessionId();
//
//            // establish REST connection
//            restPool.configureOrg(orgId, host, token);
//            RestConnection connection = restPool.getRestConnection(orgId);
//
//            return connection;
//        } catch (ApiException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//        return null;
//    }
//    
//    protected SaveResult createSalesforceLead(RestConnection connection, Map<String,String> fields) throws IOException {
//        SObject newLead = RestSObjectImpl.getNew("Lead");
//        for (String key : fields.keySet()) {
//            newLead.setField(key, fields.get(key));
//        }
//        SaveResult result = connection.create(newLead);
//        return result;
//    }
}
