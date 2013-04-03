/**************************************************************************
 Copyright (c)  2011 Arizona State University
 The following code is the property of Arizona State University and
 the Arizona Board of Regents. Various other restrictions may apply.
 Unauthorized use of this software, in whole or parts thereof, is
 strictly prohibited.

 Contact email: softwareenterprise@asu.edu
        phone: 480 727 1373.

 $Revision$
 $LastChangedBy$
 $LastChangedDate$
 $HeadURL$

**************************************************************************/

package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import beans.UserBean;
import properties.PropertiesClass;
import util.LoggerUtil;

import dao.ConnectionDAO;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The Login servlet is login page for user
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	private static Logger logger = LoggerUtil.getClassLogger();

	public Login() {

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("token");
		String decodedEmail = "";
		if (email != null) {
			if (!email.equals("")) {
				decodedEmail = decodeEmailAddress(email);
				updateEmail(decodedEmail);
			}
		}
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/static/ASU_Header.html");
		dispatcher.include(request, response);
		out.println(generateHtml(request,email));
		dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
		dispatcher.include(request, response);
	}
	
	private String decodeEmailAddress(String txtInHex)
	{
		byte [] txtInByte = new byte [txtInHex.length() / 2];
		int j = 0;
		for (int i = 0; i < txtInHex.length(); i += 2)
		{
			txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
		}
		String txt = new String(txtInByte);
		System.out.println(txt);
		return txt;
	}

	private void updateEmail(String email) {
		try {
			connection.updateTrackedEmail(email);
		} catch (Exception ex) {
			logger.error("Unable to update tracked email exception "
					+ ex.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getParameter("userId");
		HttpSession session = request.getSession(true);
		//if (true) {
		if (isUserValid(request)) {
			session.setAttribute("loggedIn", userid);
			updateLastLastLogin(userid);
			// Check if its a redirect from debriefing page
			String redir = request.getParameter("redirect");
			if (redir != null && redir.equalsIgnoreCase("thanku")) {
				String sid = (String) session.getAttribute("surveytaken");
				if (sid != null) {
					response
							.sendRedirect(response
									.encodeRedirectURL("BriefingPage?surveyid="
											+ sid));
				}
			}
			String isUserRegistered = (String) request
					.getAttribute("isUserRegistered");
			if (isUserRegistered.equalsIgnoreCase("true")) {
				session.setAttribute("userAuthenticated", "true");
				logger
						.info("user authenticated successfully-redirecting to landing page");
				response
						.sendRedirect(response
								.encodeRedirectUrl("LandingPageForUser"));
			} else {
				logger
						.info("user authenticated successfully redirecting non-registered user to QuestionsReg page");
				response.sendRedirect(response
						.encodeRedirectUrl("QuestionsReg"));
			}
		} else {
			String errors = "Invalid UserId. Please Try again!!";
			request.setAttribute("Loginerrors", errors);
			doGet(request, response);
			return;
		}
	}

	private void updateLastLastLogin(String userId) {
		connection.updateLastLastLogin(userId);
	}

	private String generateHtml(HttpServletRequest request,String email) {
		HttpSession session = request.getSession(true);
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>Login</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		String errors = (String) request.getAttribute("Loginerrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td>");
			sb.append(errors);
			sb.append("</td></tr></table>");
		}
		sb.append("<div id='content'>");
		sb.append("<h3 id='signin'> User - Sign In </h3>");
		//String email = request.getParameter("email");
		sb.append("<form id='login' name='loginForm' action='' method='post'>");
		// sb.append("UserId:(*)</td><td>");
		sb.append("<label>UserId:</label>");
		sb
				.append("<input class='textfield' type='text' name='userId' maxlength='7'><label class='star'>*</label>");
		sb
				.append(" <br> <input type='submit' name='login' value='Submit'><br> <br> ");
		sb.append("(*) asterisk indicates mandatory field<br>");
		String path = ""; // "/"+getServletContext().getServletContextName();
		sb
				.append("<br><a href='"+path+"UserIdRecovery'> I can't access my account - UserId Recovery </a>");
		sb
		.append("<br><a href='"+path+"Registration?token="+email+"'> Signup to get a new userid </a>");
		sb.append("</form></div>");
		sb.append("<br><br><br><br><br><br><br><br><br>");
		return sb.toString();
	}

	private boolean isUserValid(HttpServletRequest request) {
		String userid = request.getParameter("userId");
		HttpSession session = request.getSession(true);
		boolean isIdValid = false;
		UserBean user = connection.getUserDetails(userid);
		if (user != null) {
			isIdValid = true;
			int isUserReg = user.getIsUserRegistered();
			if (isUserReg == 1) {
				request.setAttribute("isUserRegistered", "true");
			} else {
				request.setAttribute("isUserRegistered", "false");
			}
		}
		return isIdValid;
	}

}
