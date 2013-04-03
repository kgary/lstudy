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
import javax.servlet.http.Cookie;
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
 * The LandingPageForUser servlet page shown to
 * user upon successful authentication
 */
public class LandingPageForUser extends HttpServlet {
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
		String loggedin = (String) session.getAttribute("loggedIn");
		String userAuthenticated = (String) session
				.getAttribute("userAuthenticated");
		// String loggedin ="sri1234";
		if (loggedin != null) {
			if (userAuthenticated != null) {
				Cookie c = new Cookie("userid", loggedin);
				response.addCookie(c);
				String value = request.getParameter("value");
				logger.info("value :" + value);
				if (value != null) {
					updateUserStatus(request, value);
				}
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/static/ASU_Header.html");
				dispatcher.include(request, response);
				out.println(generateHtml(request));
				dispatcher = request
						.getRequestDispatcher("/static/ASU_Footer.html");
				dispatcher.include(request, response);
			} else {
				response.sendRedirect(response
						.encodeRedirectUrl("Login"));
			}
		} else {
			response.sendRedirect(response
					.encodeRedirectUrl("Login"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String applicationContext = "";
		String userid = (String) session.getAttribute("loggedIn");
		ArrayList<String> messages = getMessageCenter(userid);
		getListOfSurveysForUser(request);
		getListOfCompletedSurveysForUser(request);
		ArrayList<SurveyBean> surveysList = (ArrayList) session.getAttribute("surveysList");
		ArrayList<SurveyBean> completedsurveysList = (ArrayList) session
				.getAttribute("CompletedsurveysList");
		SurveyBean survey;
		String url = "";
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>User Landing Page</title>");
		
		String userid1 = (String)session.getAttribute("loggedIn");
		sb.append("<br><table align='left'><tr><td>");
		sb.append("Logged in user: "+userid1+"</td></tr></table><br>");
		
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/Logout'>Logout</a>");
		sb.append("</td></tr></table>");
		
		sb.append("<table><tr><td>");
		sb.append("This is the main landing page for the research study. On this page you " +
				"can see what surveys you have completed and what surveys are in your queue." +
				" Any special instructions will be posted to the message center on the right.<br><br>" +
				"If you have any questions about the research study or the functioning of this application " +
				"please email the Principal Investigator, Dr. Kevin Gary, at kgary@asu.edu.<br><br>Thanks you for your participation");
		sb.append("</td></tr></table>");
		
		sb.append("<table><tr><td>");
		sb.append("<br>Surveys to be taken:");
		if (surveysList != null && surveysList.size() > 0) {
			
			sb.append("<table border='1' cellpadding='2' cellspacing='0'>");
			sb.append("<tr><td>Survey Name</td><td>Date Created</td></tr> ");
			for (int i = 0; i < surveysList.size(); i++) {
				survey = (SurveyBean) surveysList.get(i);
				sb.append("<tr><td>&nbsp");
				String id_value = "survey_id_" + i;
				url = survey.getSurveyURL() + "&token="
						+ (String) session.getAttribute("loggedIn");
				sb.append("<a id='" + id_value + "' href='" + url
						+ "'onclick='updateStatus(" + i + ")'>"
						+ survey.getSurveyName() + "</a>&nbsp</td>");
				sb.append("<td>" + survey.getDateCreated() + "</td></tr><br>");
			}
			sb.append("</table></td><td><br><br>");
		}
		else {
			sb
					.append("<br><br><br><br><br>Presently there are no surveys available for you. Thank you!<br><br><br><br><br>");
		}
		sb.append("<td>&nbsp</td><td>&nbsp</td><td><table><tr><td>");
		sb.append("Message from Admin:</td></tr>");
		if (messages != null && messages.size() > 0) {
			
			for (int index = 0; index < messages.size(); index++) {
				sb.append("<tr><td>");
				sb.append((String) messages.get(index));
				sb.append("</td></tr>");
			}
			sb.append("</table></td></tr></table><br><br>");

		} else {
			sb.append("<tr><td>");
			sb.append("You dont have any Messages from admin.");
			sb.append("</table></td></tr></table><br><br>");
		}
		sb.append("<br>Surveys completed:<br><br>");
		if (completedsurveysList != null && completedsurveysList.size() > 0) {
			
			sb
					.append("<table border='1' cellpadding='2' cellspacing='0'><tr><td>Survey Name:</td><td>Date Completed:</td></tr>");
			for (int i = 0; i < completedsurveysList.size(); i++) {
				survey = (SurveyBean) completedsurveysList.get(i);
				sb.append("<tr><td>");
				sb.append(survey.getSurveyName());
				sb.append("</td><td>");
				sb.append(survey.getDateCompleted() + "</td></tr>");

			}
			sb.append("</table><br><br>");
		} else {
			sb
					.append("<table><tr><td colspan='2'>You have not completed any surveys yet</td></tr>");
			sb.append("</table><br><br>");
		}
		return sb.toString();
	}

	private void getListOfSurveysForUser(HttpServletRequest request) {
		connection.getListOfSurveysForUser(request);
	}

	private void getListOfCompletedSurveysForUser(HttpServletRequest request) {
		connection.getListOfCompletedSurveysForUser(request);
	}

	private ArrayList<String> getMessageCenter(String userId) {
		return connection.getMessageCenter(userId);
	}

	private void updateUserStatus(HttpServletRequest request, String survey) {
		// Need to update user status in DB
		try {
			HttpSession session = request.getSession(true);
			String userId = (String) session.getAttribute("loggedIn");
			// String userId="sri1234";
			logger.info("AJAX sent survey url: " + survey);
			if (!connection.updateUserSurveyAccessStatus(userId, survey)) {
				String errors = "Unable to process AJAX request";
				request.setAttribute("Loginerrors", errors);
			}
		} catch (Exception ex) {
			logger.error("exception " + ex.getMessage());
		}
	}

}
