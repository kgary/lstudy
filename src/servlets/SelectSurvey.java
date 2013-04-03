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
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import beans.SurveyBean;

import dao.ConnectionDAO;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The SelectSurvey servlet is used to select
 *  a survey by admin for adding users/groups
 */
public class SelectSurvey extends HttpServlet {
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
		String selectedSurvey = request.getParameter("surveyGroup");
		logger.info("selected survey: " + selectedSurvey);
		session.setAttribute("selectedSurvey", selectedSurvey);
		response.sendRedirect(response
				.encodeRedirectUrl("TriggerEmails"));
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		SurveyBean survey;
		getAllSurveys(request);
		ArrayList surveysList = (ArrayList) session
				.getAttribute("AllsurveysList");
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>Select Survey</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<h3> Admin User - Select Survey </h3>");
		sb.append("<form name='selectSurvey' action='' method='post'>");
		sb
				.append("<label>Select Survey&nbsp&nbsp&nbsp&nbsp&nbsp</label><p/>");
		int i = 0;
		for (int index = 0; index < surveysList.size(); index++) {
			survey = (SurveyBean) surveysList.get(index);
			if (i == 0)
				sb
						.append("<input type='radio' name='surveyGroup' checked value='"
								+ survey.getSurveyName()
								+ "'>"
								+ survey.getSurveyName() + "<br>");
			else
				sb.append("<input type='radio' name='surveyGroup' value='"
						+ survey.getSurveyName() + "'>"
						+ survey.getSurveyName() + "<br>");
		}
		sb
				.append(" <br> <input type='submit' name='selectSurvey' value='Submit'>");
		sb.append("</form>");
		sb.append("<br><br><br><br><br><br><br>");
		return sb.toString();
	}

	private void getAllSurveys(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		ResultSet rs = null;
		SurveyBean survey;
		ArrayList surveysList = connection.getAllSurveys();
		session.setAttribute("AllsurveysList", surveysList);
	}

}
