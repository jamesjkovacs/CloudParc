package cloudparc;

import com.google.appengine.api.datastore.*;
import java.util.*; 


public class QRCodeXmlRPC 
{
	
	public boolean checkValidUser(String userName, String password) 
	{
		if (userName == null || password == null)
			return false;

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query q = new Query("User");
		q.addFilter("email", Query.FilterOperator.EQUAL, userName);
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		if (result != null) {
			String qEmail = (String) result.getProperty("email");
			String qPassword = (String) result.getProperty("password");

			if (qEmail.equals(userName) && qPassword.equals(password))
				return true;
		}
		return false;
	}
	
	public Map getQrCodeURL(String userName, String password)
	{
		HashMap map = new HashMap() ;
		if (checkValidUser(userName, password))
		{
			map.put("data", userName);
			return map ;
		}
		
		return map ;
	}
	
}
