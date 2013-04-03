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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import properties.PropertiesClass;
import util.LoggerUtil;

import dao.ConnectionDAO;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The Admin servlet is login for admin
 * part of the application.
 */
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	private static Logger logger = LoggerUtil.getClassLogger();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/static/ASU_Header.html");
		dispatcher.include(request, response);
		out.println(generateHtml(request));
		dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
		dispatcher.include(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("auserId");
		String password = request.getParameter("apswd");
		HttpSession session = request.getSession(true);
		String errors = validateDetails(userId, password);
		if (errors != null && errors.length() > 0) {
			request.setAttribute("adminErrors", errors);
			doGet(request, response);
			return;
		}
		boolean isUserValid = getUserIdPasswordFromDb(userId, password);
		if (isUserValid) {
			session.setAttribute("admin", userId);
			response.sendRedirect(response
					.encodeRedirectUrl("AdminHome"));
		} else {
			errors = "Invalid userid/password. Please try again";
			request.setAttribute("adminErrors", errors);
			doGet(request, response);
			return;
		}
	}

	private String generateHtml(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>AdminLogin</title>");
		// sb.append("<body bgcolor='FFCC66'>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		String errors = (String) request.getAttribute("adminErrors");
		if (!isEmpty(errors)) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<div id='content'");
		sb.append("<h3 id='signin'> Admin User - Sign In </h3>");
		sb
				.append("<form id='login'name='adminLoginForm' action='' method='post'>");
		sb
				.append("<label>UserId&nbsp&nbsp&nbsp&nbsp&nbsp:</label>");
		sb
				.append("<input class='textfield' type='text' name='auserId' maxlength='7'><label class='star'>*</label><br>");
		sb.append("<label>Password:</label>");
		sb
				.append("<input class='textfield' type='password' name='apswd' maxlength='7'><label class='star'>*</label><br>");
		sb
				.append(" <br> <input type='submit' name='alogin' value='Submit'><br>");
		sb.append("<br> (*) asterisk indicates mandatory field");
		sb.append("</form></div>");
		sb.append("<br><br><br><br><br><br><br>");
		return sb.toString();
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

	private boolean isUserIdValid(String userId) {
		String regex = "[A-Za-z0-9]*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(userId);
		return m.matches();
	}

	private boolean getUserIdPasswordFromDb(String userId, String password) {
		return connection.getAdminCredentials(userId, password);
	}

	private String validateDetails(String userId, String password) {
		StringBuffer errorsSb = new StringBuffer("");
		if (isEmpty(userId)) {
			errorsSb.append("Please provide userid <br>");
		} else {
			if (!isUserIdValid(userId)) {
				errorsSb
						.append("userId should have numbers and alphabets only<br>");
			}
		}
		if (isEmpty(password)) {
			errorsSb.append("Please provide password <br>");
		}
		return errorsSb.toString();
	}

}
