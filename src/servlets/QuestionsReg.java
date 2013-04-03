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
 * The QuestionsReg servlet is questions page shown to
 *  user during registration
 */
public class QuestionsReg extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ArrayList questionsList = new ArrayList();
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

		HttpSession session = request.getSession(true);
		String loggedin = (String) session.getAttribute("loggedIn");
		if (loggedin != null) {
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("cancel") != null) {
			response.sendRedirect(response
					.encodeRedirectUrl("Login"));
			return;
		}
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("loggedIn");
		String validationErrors = validateDetails(request);
		if (validationErrors != null && validationErrors.length() > 0) {
			request.setAttribute("questionsRegErrors", validationErrors);
			doGet(request, response);
			return;
		}
		connection.registerTheUser(request, userId);
		String errors = (String) session.getAttribute("registerTheUserErrors");
		logger.info("registration of new user status: " + errors);
		if (errors.equalsIgnoreCase("Registration succesfull")) {
			logger
					.info("new user registration successful-redirecting to landing page");
			session.setAttribute("userAuthenticated", "true");
			response
					.sendRedirect(response
							.encodeRedirectUrl("LandingPageForUser"));
		} else {
			request.setAttribute("questionsRegErrors", errors);
			doGet(request, response);
			return;
		}
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

	private boolean isAnswerValid(String answer) {
		String regex = "[A-Za-z0-9 A-Za-z0-9]*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(answer);
		return m.matches();
	}

	private String validateDetails(HttpServletRequest request) {
		StringBuffer errorsSb = new StringBuffer("");
		String question1 = request.getParameter("q1dropdown");
		String question2 = request.getParameter("q2dropdown");
		String question3 = request.getParameter("question3");
		String ans1 = request.getParameter("ans1");
		String ans2 = request.getParameter("ans2");
		String ans3 = request.getParameter("ans3");
		if (question1.equalsIgnoreCase("Select")) {
			errorsSb.append("Please select question1 <br>");
		}
		if (question2.equalsIgnoreCase("Select")) {
			errorsSb.append("Please select question2 <br>");
		}
		if (isEmpty(question3)) {
			errorsSb.append("Please select question3 <br>");
		}

		if (isEmpty(ans1)) {
			errorsSb.append("Please provide answer for question 1<br>");
		}
		if (isEmpty(ans2)) {
			errorsSb.append("Please provide answer for question 2<br>");
		}
		if (isEmpty(ans3)) {
			errorsSb.append("Please provide answer for question 3<br>");
		}
		if (!isAnswerValid(ans1)) {
			errorsSb.append("Answer 1 can have alphabets and digits only<br>");
		}
		if (!isAnswerValid(ans2)) {
			errorsSb.append("Answer 2 can have alphabets and digits only<br>");
		}
		if (!isAnswerValid(ans3)) {
			errorsSb.append("Answer 3 can have alphabets and digits only<br>");
		}
		if (isEmpty(errorsSb.toString())) {
			if (question1.equalsIgnoreCase(question2))
				errorsSb.append("Both the security questions are same."
						+ " Please select 2 different questions");
		}
		return errorsSb.toString();
	}

	private String generateHtml(HttpServletRequest request) {
		questionsList.clear();
		getQuestionFromDB();
		StringBuilder sb = new StringBuilder("");
		String strTemp = "";
		sb.append("<title>Registration</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>New User - Registration</h3>");
		String errors = (String) request.getAttribute("questionsRegErrors");
		if (!isEmpty(errors)) {
			sb.append("<table><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<form name='regForm' action='' method='post'>");
		sb.append("<table>");
		sb.append("<tr><td>Question1:</td><td>");
		sb.append("<select name='q1dropdown' id='q1dropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(request.getParameter("q1dropdown"))) {
			for (int i = 0; i < questionsList.size(); i++) {
				sb.append("<option value='" + (String) questionsList.get(i)
						+ "'>" + (String) questionsList.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < questionsList.size(); i++) {
				strTemp = (String) questionsList.get(i);
				if (strTemp
						.equalsIgnoreCase(request.getParameter("q1dropdown"))) {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "' selected='true'>"
							+ (String) questionsList.get(i) + "</option>");
				} else {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "'>" + (String) questionsList.get(i)
							+ "</option>");
				}
			}
		}
		sb.append("</select><label class='star'>*</label>");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans1"))) {
			sb.append("<input type='text' size='30' name='ans1'>&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans1' value='"
					+ request.getParameter("ans1") + "'>&nbsp");
		}
		sb.append("<label class='star'>*</label></td></tr>");
		sb.append("<tr><td>Question2:</td><td>");
		sb.append("<select name='q2dropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(request.getParameter("q2dropdown"))) {
			for (int i = 0; i < questionsList.size(); i++) {
				sb.append("<option value='" + (String) questionsList.get(i)
						+ "'>" + (String) questionsList.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < questionsList.size(); i++) {
				strTemp = (String) questionsList.get(i);
				if (strTemp
						.equalsIgnoreCase(request.getParameter("q2dropdown"))) {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "' selected='true'>"
							+ (String) questionsList.get(i) + "</option>");
				} else {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "'>" + (String) questionsList.get(i)
							+ "</option>");
				}
			}
		}
		sb.append("</select><label class='star'>*</label>");
		sb.append("</td></tr>");

		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans2"))) {
			sb.append("<input type='text' size='30' name='ans2' >&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans2' value='"
					+ request.getParameter("ans2") + "'>&nbsp");
		}
		sb.append("<label class='star'>*</label></td></tr>");
		sb.append("<tr colspan='2'><td>Please enter your own question here"
				+ "</td></tr>");
		sb.append("<tr><td>Question3:</td><td>");
		if (isEmpty(request.getParameter("question3"))) {
			sb.append("<input type='text' size='30' name='question3' >&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='question3' value='"
					+ request.getParameter("question3") + "'>&nbsp");
		}
		sb.append("<label class='star'>*</label></td></tr>");

		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans3"))) {
			sb.append("<input type='text' size='30' name='ans3' >&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans3' value='"
					+ request.getParameter("ans3") + "'>&nbsp");
		}
		sb.append("<label class='star'>*</label></td></tr>");

		sb.append("<tr><td>");
		sb.append("<input type='submit' name='submit' value='Submit'>&nbsp");
		sb.append("</td><td>");
		sb.append("<input type='submit' name='cancel' value='Cancel'>&nbsp");
		sb.append("</td></tr></table>");
		sb.append("<table><tr><td>");
		sb.append("(*) asterisk indicates mandatory field");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("</form>");
		return sb.toString();
	}

	private void getQuestionFromDB() {
		questionsList = connection.getQuestionsFromDb();
	}

}
