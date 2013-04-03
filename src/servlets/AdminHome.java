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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The AdminHome servlet is landing page for
 * admin
 */
public class AdminHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
			out.println(generateHtml());
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
	}

	private String generateHtml() {
		StringBuilder sb = new StringBuilder("");
		String applicationContext = "";
		sb.append("<title>AdminHome</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>Admin Options</h3>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<ul type='disc'><li>");
		sb
				.append("<a href='"+applicationContext+"AddSurveyDetails'>Add Survey</a>");
		sb.append("</li><li>");
		sb
				.append("<a href='"+applicationContext+"AddUserToSurvey'>Add Surveys To User</a>");
		sb.append("</li><li>");
		sb
				.append("<a href='"+applicationContext+"SelectSurvey'>Trigger Emails</a>");
		sb.append("</li><li>");
		sb
				.append("<a href='"+applicationContext+"ResetSecretQuestions'>Reset Secret questions for user</a>");
		sb.append("</li><li>");
		sb
				.append("<a href='"+applicationContext+"MessageCenter'>Message Center</a>");
		sb.append("</li><li>");
		sb
				.append("<a href='"+applicationContext+"ReportsFilter'>Generate Reports By Filters</a>");
		sb.append("</li><li>");
		sb.append("<a href='"+applicationContext+"ReportsByGroups'>Generate Reports By Groups</a>");
		// .append("<a href='#' DISABLED='true' title='Disbaled this link as groups manager app is not properly designed'>Generate Reports By Groups</a>");
		sb.append("</li></ul><br><br><br><br><br><br><br>");
		return sb.toString();
	}

}
