package cloudparc;

import java.io.IOException;
import javax.servlet.http.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import java.util.*; 

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	 }

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String authorized = (String) req.getSession()
				.getAttribute("authorized");
		String userName = (String) req.getParameter("email1");
		String password = (String) req.getParameter("password");

		if (authorized == null) {
			req.getSession().setAttribute("authorized", "no");
			authorized = (String) req.getSession().getAttribute("authorized");
		}

		if (checkValidUser(userName, password) == true) {
			// Redirect to main operation servlet!
			req.getSession().setAttribute("authorized", "yes");
			sendQrCodeEmail(userName);
			emitSuccessfulPage(resp, userName);
		} else {
			req.getSession().setAttribute("authorized", "no");
			emitLoginPage(resp, userName);
		}
	}

	boolean checkValidUser(String userName, String password) {
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

	void emitSuccessfulPage(HttpServletResponse resp, String email)
			throws IOException {
		PrintWriter out = resp.getWriter();
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
		out.println("        <h1>Thank you!<h2>");
		out.println("        <p>We have sent an email with your QRCode to the following adddress: "
				+ email + " <a href=\"http:////cloudparking.appspot.com/login\">Continue...</a></p>");
		out.println("    </DIV>");
		out.println("</body>");
		out.println("</html>		");
		out.println("");
	}

	void emitLoginPage(HttpServletResponse resp, String emailAddress)
			throws IOException {
		PrintWriter out = resp.getWriter();

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
		out.println("        <form id=\"loginform\" name=\"loginform\" method=\"post\" action=\"/login\">");
		out.println("            <h1>Log In</h1>");
		out.println("            <p>");
		out.println("            Not a member yet? <A HREF=\"/register\">Join for free!</A>");
		out.println("            </p>");
		out.println("");
		out.println("            <label>");
		out.println("            email");
		out.println("            <span class=\"small\">");
		out.println("            </span>");
		out.println("            </label>");
		out.println("            <input type=\"text\" name=\"email\" id=\"email\" />");
		out.println("");
		out.println("            <label>");
		out.println("            password");
		out.println("            <span class=\"small\">");
		out.println("            <A HREF=\"/resendpassword\">Forgot your Password?</A>");
		out.println("            </span>");
		out.println("            </label>");
		out.println("            <input type=\"text\" name=\"password\" id=\"password\" />");
		out.println("");
		out.println("            <button type=\"submit\">Sign-up</button>");
		out.println("            <div class=\"spacer\">");
		out.println("            </div>");
		out.println("        </form>");
		out.println("    </DIV>");
		out.println("</body>");
		out.println("</html>		");
		out.println("");
	}


	void sendQrCodeEmail(String email) {
		URL url = null;
		
		
		try {
			url = new URL("http://chart.apis.google.com/chart?chs=200x200&cht=qr&chl=" + email);
			
		} catch (MalformedURLException e1) {
		}
		
		
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		InputStream is = null;
		try {
		  is = url.openStream ();
		  byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    bais.write(byteChunk, 0, n);
		  }
		}
		catch (IOException e) 
		{
		}
		finally {
		  if (is != null) 
		  { 
			  try 
			  {
				  is.close();
			  } catch (IOException e)
			  {

			  } 
		  }
		}
		
		
		byte[] picture = bais.toByteArray();
		MailService mailService = MailServiceFactory.getMailService();
		MailService.Message message = new MailService.Message();
		message.setSender("vijay.parikh@gmail.com");
		message.setSubject("QR-Code");
		message.setTo(email);
		message.setHtmlBody("<HTML><body><h1>Present this QR-Code when you park.</h1><img src='cid:pic.png'></body></HTML>");
		MailService.Attachment attachment =
		        new MailService.Attachment("pic.png", picture);
		message.setAttachments(attachment);
		
		try {
		mailService.send(message);	
		}
		catch (IOException ex)
		{
		}
	}
}
