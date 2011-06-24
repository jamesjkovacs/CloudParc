package cloudparc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.* ;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;

import java.io.* ;


public class RegisterServlet extends HttpServlet 
{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		doPost(req, resp) ;
 	}
	
	boolean checkValidUser(String userName) 
	{
		if (userName == null)
			return false;

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query q = new Query("User");
		q.addFilter("email", Query.FilterOperator.EQUAL, userName);
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		if (result != null) {
			String qEmail = (String) result.getProperty("email");

			if (qEmail.equals(userName))
				return true;
		}
		return false;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String authorized = (String)req.getSession().getAttribute("authorized");
		String userName = (String)req.getParameter("email");
		String password = (String)req.getParameter("password");
		String password1 = (String)req.getParameter("password2");
		String firstName = (String)req.getParameter("firstname");
		String lastName = (String)req.getParameter("lastname"); 
		
		if (userName != null)
		{
			if (userName != null && password != null && password1 != null && firstName != null && lastName != null)
			{
				if (!checkValidUser(userName))
				{
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
					Entity employee = new Entity("User");
					employee.setProperty("email", userName) ;
					employee.setProperty("password", password);
					employee.setProperty("firstName", firstName);
					employee.setProperty("lastName", lastName);
					datastore.put(employee);	
				}
			}
				
			resp.sendRedirect("/login");
		}
		emitRegisterationPage(resp) ;
	}
	

	void emitRegisterationPage(HttpServletResponse resp) throws IOException
	{
		PrintWriter out = resp.getWriter() ;
		out.println("<html>");
		out.println("<head>");
		out.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"/styles/login.css\"");
		out.println("</head>");
		out.println("");
		out.println("<body>");
		out.println("    <div id=\"logo\" CLASS=\"logo\">");
		out.println("        <IMG SRC=\"/images/orange-car-icon.png\" WIDTH=\"200\" HEIGHT=\"200\"></img>");
		out.println("    </DIV>");
		out.println("    ");
		out.println("    <div id=\"formdialog\" class=\"myform\">");
		out.println("        <form id=\"registerform\" name=\"registerform\" method=\"post\" action=\"/register\">");
		out.println("            <h1>Register</h1>");
		out.println("            <p>");
		out.println("            Create a new account...");
		out.println("            </p>");
		out.println("            <label>");
		out.println("            First Name");
		out.println("            </label>");
		out.println("            <input type=\"text\" name=\"firstname\" id=\"firstname\" />");
		out.println("");
		out.println("            <label>");
		out.println("            Last Name");
		out.println("            </label>");
		out.println("            <input type=\"text\" name=\"lastname\" id=\"lastname\" />");
		out.println("");
		out.println("            <label>");
		out.println("            email");
		out.println("            <span class=\"small\">");
		out.println("            </span>");
		out.println("            </label>");
		out.println("            <input type=\"text\" name=\"email\" id=\"email\" />");
		out.println("");
		out.println("            <label>");
		out.println("            Password");
		out.println("            </label>");
		out.println("            <input type=\"text\" name=\"password\" id=\"password\" />");
		out.println("");
		out.println("            <label>");
		out.println("            Retype Password");
		out.println("            </label>");
		out.println("            <input type=\"text\" name=\"password2\" id=\"password2\" />");
		out.println("");
		out.println("            <button type=\"submit\">Register</button>");
		out.println("            <div class=\"spacer\">");
		out.println("            </div>");
		out.println("        </form>");
		out.println("    </DIV>");
		out.println("</body>");
		out.println("</html>		");
		out.println("");
	}
}
