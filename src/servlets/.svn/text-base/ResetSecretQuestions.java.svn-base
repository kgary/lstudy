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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import beans.UserBean;

import dao.ConnectionDAO;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The ResetSecretQuestions servlet is used to reset
 *  secret questions for user by admin 
 */
public class ResetSecretQuestions extends HttpServlet {
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
		String userid = request.getParameter("userId");
		if (isEmpty(userid)) {
			request.setAttribute("resetSecretQuestionsErrors",
					"Please provide seven digit userid");
			doGet(request, response);
			return;
		}
		String errors = "";
		if (isUserIdValid(userid)) {
			int i = resetUsersSecretQuestions(userid);
			if (i == 1) {
				errors = "reset succesfull";
			} else {
				errors = "resetting failed. Please try again";
			}
		} else {
			errors = "Invalid userid. Please try again";
		}
		logger.info("user secret questions re-set status: " + errors);
		request.setAttribute("resetSecretQuestionsErrors", errors);
		doGet(request, response);
		return;
	}

	private boolean isUserIdValid(String userid) {
		boolean isIdValid = false;
		ResultSet rs = null;
		UserBean user = connection.getUserDetails(userid);
		try {
			if (user != null) {
				isIdValid = true;
			}
		} catch (Exception ex) {
			logger.error("exception " + ex.getMessage());
		}
		return isIdValid;
	}

	private int resetUsersSecretQuestions(String userid) {
		return connection.resetUsersSecretQuestions(userid);
	}

	private String generateHtml(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder("");
		String applicationContext = "";
		sb.append("<title>ResetSecretQuestions for user</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<a href='"+applicationContext+"/AdminHome'>Home</a>");
		sb.append("<h3 id='pagetitle'>Reset secret questions for user</h3>");
		String errors = (String) request
				.getAttribute("resetSecretQuestionsErrors");
		if (!isEmpty(errors)) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<div id='content'");
		sb
				.append("<form id='resetSecretQuestions' name='resetSecretQuestions' action='' method='post'>");
		sb
				.append("<br><label>UserId&nbsp&nbsp&nbsp&nbsp&nbsp:</label>");
		sb
				.append("<input class='textfield' type='text' name='userId' maxlength='7'><label class='star'>*</label><br>");
		sb
				.append(" <br> <input type='submit' name='alogin' value='Submit'><br>");
		sb.append(" <br> (*) asterisk indicates mandatory field");
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

}
