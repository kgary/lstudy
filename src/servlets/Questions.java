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

import beans.QuestionsBean;

import properties.PropertiesClass;
import util.LoggerUtil;

import dao.ConnectionDAO;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The Questions servlet is page where users
 * questions are shown for authentication purposes
 */
public class Questions extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	private ArrayList questionsList = new ArrayList();
	private ArrayList answersList = new ArrayList();
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
		String userId = (String) session.getAttribute("loggedIn");
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
		String ans1 = request.getParameter("ans1");
		String ans2 = request.getParameter("ans2");
		String ans = request.getParameter("ans");
		String validationErrors = validateDetails(ans1, ans2, ans);
		if (validationErrors != null && validationErrors.length() > 0) {
			request.setAttribute("questionsErrors", validationErrors);
			logger.info("error while validating answers: " + validationErrors);
			doGet(request, response);
			return;
		}
		if (ans1.trim().equalsIgnoreCase((String) answersList.get(0))) {
			if (ans2.trim().equalsIgnoreCase((String) answersList.get(1))) {
				String a = (String) session.getAttribute("answer");
				if (a !=null && ans.trim().equalsIgnoreCase(a.trim())) {
					session.setAttribute("userAuthenticated", "true");
					logger
							.info("user authenticated successfully-redirecting to landing page");
					response
							.sendRedirect(response
									.encodeRedirectUrl("LandingPageForUser"));
				} else {
					validationErrors = "answer to secret question seems to be wrong."
							+ " Please try again!!";
				}
			} else {
				validationErrors = "answer to question2 seems to be wrong."
						+ " Please try again!!";
			}
			request.setAttribute("questionsErrors", validationErrors);
			doGet(request, response);
			return;
		} else {
			validationErrors = "answer to question1 seems to be wrong. "
					+ "Please try again!!";
			request.setAttribute("questionsErrors", validationErrors);
			doGet(request, response);
			return;
		}
	}

	private boolean isAnswerValid(String answer) {
		String regex = "[A-Za-z0-9 A-Za-z0-9]*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(answer);
		return m.matches();
	}

	private String validateDetails(String ans1, String ans2, String ans) {
		StringBuffer errorsSb = new StringBuffer("");

		if (isEmpty(ans1)) {
			errorsSb.append("Please provide answer for question1 <br>");
		} else {
			if (!isAnswerValid(ans1)) {
				errorsSb
						.append("Please correct answer 1."
								+ " Answer can consist of alphabets and numbers only <br>");
			}
		}

		if (isEmpty(ans2)) {
			errorsSb.append("Please provide answer for question2 <br>");
		} else {
			if (!isAnswerValid(ans2)) {
				errorsSb
						.append("Please correct answer 2."
								+ " Answer can consist of alphabets and numbers only <br>");
			}
		}

		if (isEmpty(ans)) {
			errorsSb.append("Please provide answer for secret question <br>");
		} else {
			if (!isAnswerValid(ans)) {
				errorsSb
						.append("Please correct answer for secret question. "
								+ "Answer can consist of alphabets and numbers only <br>");
			}
		}

		return errorsSb.toString();
	}

	private String generateHtml(HttpServletRequest request) {

		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("loggedIn");
		getSecretQuestions(userId);
		getUserQuestionFromDB(request);
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>User Confirmation</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>Sign In - Confirmation </h3>");
		sb.append("<form name='confirmationForm' action='' method='post'>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+"Logout'>Logout</a>");
		sb.append("</td></tr></table>");
		String errors = (String) request.getAttribute("questionsErrors");
		if (!isEmpty(errors)) {
			sb.append("<table><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<table>");
		sb.append("<tr><td>Question1:</td><td>");
		sb.append(questionsList.get(0) + "&nbsp");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans1"))) {
			sb.append("<input type='text' size='30' name='ans1'>&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans1' value='"
					+ request.getParameter("ans1") + "'>&nbsp");
		}
		sb.append("<label class='star'>*</label></td></tr><tr/>");
		sb.append("<tr><td>Question2:</td><td>");
		sb.append(questionsList.get(1) + "&nbsp");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans2"))) {
			sb.append("<input type='text' size='30' name='ans2'>&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans2' value='"
					+ request.getParameter("ans2") + "'>&nbsp");
		}
		sb.append("<label class='star'>*</label></td></tr><tr/>");
		sb.append("<tr><td>Secret Question:</td><td>");
		sb.append((String) session.getAttribute("question") + "&nbsp");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans"))) {
			sb.append("<input type='text' size='30' name='ans'>&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans' value='"
					+ request.getParameter("ans") + "'>&nbsp");
		}
		sb.append("<label class='star'>*</label></td></tr><tr/>");

		sb.append("<tr><td>");
		sb.append("<input type='submit' name='confirm' value='Submit'>&nbsp");
		sb.append("</td><td>");
		sb.append("<input type='submit' name='cancel' value='Cancel'>&nbsp");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("<table><tr><td>");
		sb.append("(*) asterisk indicates mandatory field");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("</form><br>");
		return sb.toString();
	}

	private void getUserQuestionFromDB(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("loggedIn");
		QuestionsBean ques = null;
		ques = connection.getUserQuestionFromDb(userId);
		if (ques != null) {
			session.setAttribute("question", ques.getOwnQuestion());
			session.setAttribute("answer", ques.getOwnAnswer());
		}
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

	private void getSecretQuestions(String userId) {
		answersList.clear();
		questionsList.clear();
		QuestionsBean ques = null;
		ques = connection.getSecretQuestionsOfUser(userId);
		if (ques != null) {
			questionsList.add(ques.getQuestion1());
			questionsList.add(ques.getQuestion2());
			answersList.add(ques.getAnswer1());
			answersList.add(ques.getAnswer2());
		}
	}

}
