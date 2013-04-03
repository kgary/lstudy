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


import beans.EmailContentBean;

import dao.ConnectionDAO;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The TriggerEmails servlet is used to trigger
 *  emails to users
 */
public class TriggerEmails extends HttpServlet {
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
		String selectedSurvey = (String) session.getAttribute("selectedSurvey");
		if (userId != null) {
			selectedSurvey="test";
			if (selectedSurvey != null) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/static/ASU_Header.html");
				dispatcher.include(request, response);
				out.println(generateHtml(request));
				dispatcher = request
						.getRequestDispatcher("/static/ASU_Footer.html");
				dispatcher.include(request, response);
			} else {
				response.sendRedirect(response
						.encodeRedirectUrl("SelectSurvey"));
			}
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
		String subject = request.getParameter("subject");
		String body = request.getParameter("emailContent");
		ArrayList<String> emailIds = getAllEmailIds();
		session.setAttribute("subject", subject);
		session.setAttribute("body", body);
		session.setAttribute("emailIds", emailIds);
		response.sendRedirect(response
				.encodeRedirectUrl("InstructionsToAdmin"));
	}

	private ArrayList<String> getAllEmailIds() {
		ArrayList<String> emailIds = connection.getAllEmailIds();
		return emailIds;
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		getEmailContent(request);
		String subject = (String) session.getAttribute("emailSubject");
		String body = (String) session.getAttribute("emailbody");

		StringBuilder sb = new StringBuilder("");
		sb.append("<title>TriggerEmails</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		String errors = (String) request.getAttribute("adminErrors");
		if (!isEmpty(errors)) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<h3> Admin - Trigger Emails </h3>");

		sb.append("<form name='triggerEmails' action='' method='post'>");
		sb
				.append("<label>Subject&nbsp&nbsp&nbsp&nbsp&nbsp:</label>");
		sb.append("<input class='textfield' type='text' name='subject' value='"
				+ subject + "'><label class='star'>*</label>");
		sb.append("<p></p><label>Email Content</label>");
		sb
				.append("<textarea class='multitext' name='emailContent' cols='40' rows='6'>"
						+ body
						+ "</textarea><label class='star'>*</label><br>");
		sb
				.append("<br> <input type='submit' name='emails' value='Continue'>");
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

	private void getEmailContent(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		EmailContentBean email = connection.getEmailContent();
		session.setAttribute("emailSubject", email.getSubject());
		session.setAttribute("emailbody", email.getBody());
	}

}
