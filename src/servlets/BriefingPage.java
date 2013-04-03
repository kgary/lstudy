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
 * The BriefingPage servlet is page the user
 * is taken to after completing the survey.
 */
public class BriefingPage extends HttpServlet {
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
		String userid = null;
		String sid = request.getParameter("surveyid");
		session.setAttribute("surveytaken", sid);
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("userid")) {
					userid = cookies[i].getValue();
					break;
				}
			}
		}
		if ((userid == null && loggedin == null) || sid == null) {
			// redirect to login page
			response
					.sendRedirect(response
							.encodeRedirectUrl("Login?redirect=thanku"));

		} else {
			String value = null;
			if (userid != null)
				value = userid;
			else if (loggedin != null)
				value = loggedin;
			int surveyid = Integer.parseInt(sid);
			//update survey completed status of user
			updateUserSurveyStatus(request, value, surveyid);

		}
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

	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String applicationContext = "";
		session.setAttribute("userAuthenticated", "true");
		getThankYouMessage(request);
		String url = "";
		StringBuilder sb = new StringBuilder("");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		
		String userid1 = (String)session.getAttribute("loggedIn");
		sb.append("<br><table align='left'><tr><td>");
		sb.append("Logged in user: "+userid1+"</td></tr></table><br>");
		
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/Logout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<title>Thank you</title>");
		sb.append("<table>");
		String errors = (String) request.getAttribute("Loginerrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td>");
			sb.append(errors);
			sb.append("</td></tr></table>");
		}

		// Thank You message

		String message = (String) session.getAttribute("message");
		if (message != null) {
			sb.append("<label>" + message + "</label>");
		}
		sb.append("<br><br><table align='center'><tr><td>");
		sb
				.append("<a href='"+applicationContext+"/LandingPageForUser'>Click Here</a>"
						+ " to goto Landing page");
		sb.append("</td></tr></table><br><br><br>");
		return sb.toString();
	}

	private void getThankYouMessage(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String sid = request.getParameter("surveyid");
		if (sid != null) {
			int surveyid = Integer.parseInt(sid);
			String messsage = connection.getEndMessage(surveyid);
			session.setAttribute("message", messsage);
		}
	}

	private void updateUserSurveyStatus(HttpServletRequest request,
			String userid, int surveyid) {
		// Need to update user status in DB
		try {
			if (!connection.updateUserSurveyCompleteStatus(userid, surveyid)) {
				String errors = "Unable to process Survey completion request";
				request.setAttribute("Loginerrors", errors);
			}
		} catch (Exception ex) {
			logger.error("exception " + ex.getMessage());
		}
	}

}
