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
 * The AddUserToSurvey servlet is used to add/remove 
 * users/groups to survey
 */
public class AddUserToSurvey extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	private ArrayList<String> users = new ArrayList();
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
		int i = 0;
		String str;
		ArrayList<SurveyBean> surveys = (ArrayList) session.getAttribute("surveysList");
		for (; i < surveys.size(); i++) {
			str = request.getParameter("AddUsers" + i);
			if (str != null && str.equals("AddUsers")) {
				break;
			}
		}
		SurveyBean surveybean = (SurveyBean) surveys.get(i);
		String surveyName = surveybean.getSurveyName();
		session.setAttribute("surveyName", surveyName);
		ArrayList<String> users = getListOfUsersForSurvey(surveyName);

		if (users != null) {
			response.sendRedirect(response
					.encodeRedirectUrl("Users"));
		}
	}

	private ArrayList<String> getListOfUsersForSurvey(String surveyName) {
		ArrayList<String> users = connection.getListOfUsersForSurvey(surveyName);
		return users;
	}

	private String validateDetails(String[] selectedUsers,
			String[] selectedSurveys) {
		StringBuffer errorsSb = new StringBuffer("");
		if (selectedUsers == null) {
			errorsSb.append("Please select atleast one user<br>");
		}
		if (selectedSurveys == null) {
			errorsSb.append("Please select atleast one survey<br>");
		}
		return errorsSb.toString();
	}

	private String generateHtml(HttpServletRequest request) {
		users.clear();
		String applicationContext = "";
		HttpSession session = request.getSession(true);
		getAllSurveys(request);
		ArrayList<SurveyBean> surveys = (ArrayList) session.getAttribute("surveysList");
		getSurveyToolInfo(request);
		String adminURL = (String) session.getAttribute("adminURL");
		SurveyBean surveybean;
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>AddUser</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<a href='"+applicationContext+"/AdminHome'>Home</a>");
		sb.append("<table align='right'><tr><td><a href='" + adminURL
				+ "'>Admin Lime Survey</a>&nbsp</td><td>");
		sb.append("<a href='"+applicationContext+"/AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<h3 id='pagetitle'>Add Users To Survey </h3>");
		String errors = (String) request.getAttribute("addSurveyToUserErrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<form name='addSurveysForm' action='' method='post'>");
		sb.append("<table border='1' align='center'>");
		for (int index = 0; index < surveys.size(); index++) {
			surveybean = (SurveyBean) surveys.get(index);
			sb.append("<tr class='add'><td>");
			sb.append(surveybean.getSurveyName());
			sb.append("</td><td>");
			sb.append(surveybean.getSurveyDescription());
			sb.append("</td><td>");
			sb.append("<input type='submit' name='AddUsers" + index
					+ "' value='AddUsers'/>&nbsp");
		}
		sb.append("</table>");
		sb.append("</form><br><br><br>");
		return sb.toString();
	}

	private void getSurveyToolInfo(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String adminURL = connection.getSurveyToolInfo();
		session.setAttribute("adminURL", adminURL);
	}

	private void getAllSurveys(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		ArrayList<SurveyBean> surveys = connection.getAllSurveys();
		session.setAttribute("surveysList", surveys);
	}

}
