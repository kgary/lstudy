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
import java.util.ArrayList;

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
 * The MessageCenter servlet is page shown to
 * admin for adding messages to be shown to user
 */
public class MessageCenter extends HttpServlet {
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

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");

		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");
		if (userId != null) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/static/ASU_Header.html");
			dispatcher.include(request, response);
			out.println(generateHtml(request));
			dispatcher = request
					.getRequestDispatcher("/static/ASU_Footer.html");
			dispatcher.include(request, response);
		} else {
			response.sendRedirect(response
					.encodeRedirectUrl("Admin"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");
		if (userId == null) {
			doGet(request, response);
			return;
		}
		String[] selectedUsers = request.getParameterValues("usersddl");
		String message = request.getParameter("message");
		String errors = validateUserInput(selectedUsers, message);
		if (errors != null && errors.length() > 0) {
			request.setAttribute("messageCenterErrors", errors);
			doGet(request, response);
			return;
		}
		int i = saveMessage(selectedUsers, message);
		if (i == 1) {
			errors = "Save successfull";
		} else {
			errors = "Please try again";
		}
		logger.info("save message status: " + errors);
		request.setAttribute("messageCenterErrors", errors);
		doGet(request, response);
		return;
	}

	private int saveMessage(String[] selectedUsers, String message) {
		return connection.saveMessage(selectedUsers, message);
	}

	private String validateUserInput(String[] selectedUsers, String message) {
		StringBuilder errorsSB = new StringBuilder("");
		if (selectedUsers == null) {
			errorsSB.append("Please select atleast one user<br>");
		}
		if (message == null || message.length() == 0) {
			errorsSB.append("Please provide message<br>");
		}
		return errorsSB.toString();
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String applicationContext = "";
		ArrayList<String> users = getAllUsers();
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>Message to users</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<table><tr><td>");
		sb
				.append("<a href='"+applicationContext+"/AdminHome'>Home</a></td></tr></table>");
		sb.append("<h3 id='pagetitle'>Message to users</h3>");

		String errors = (String) request.getAttribute("messageCenterErrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<form name='messageToUsersForm' action='' method='post'>");
		sb.append("<table align='center'><tr><td>");
		sb.append("<label>Users</label></td></tr>");
		sb.append("<tr><td>");
		sb.append("<select class='dropdown' name='usersddl' multiple>");
		for (int index = 0; index < users.size(); index++) {
			sb.append("<option value='" + (String) users.get(index) + "'>"
					+ (String) users.get(index) + "</option>");
		}
		sb.append("</select>");
		sb.append("</td></tr>");
		sb
				.append("<tr><td><label>Message&nbsp&nbsp&nbsp&nbsp&nbsp:</label></td></tr>");
		sb
				.append("<tr><td><textarea class='multitext' name='message' cols='40' rows='6'"
						+ "></textarea><label class='star'>*</label></td></tr><br>");
		sb
				.append("<tr><td><input type='submit' name='emails' value='Submit'></td></tr><br>");
		sb
				.append("<tr><td>(*) asterisk indicates mandatory field</td></tr></form></table>");
		return sb.toString();
	}

	private ArrayList<String> getAllUsers() {
		ArrayList<String> users = connection.getAllUsers();
		return users;
	}

}
