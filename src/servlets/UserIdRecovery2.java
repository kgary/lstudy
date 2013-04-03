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

import dao.ConnectionDAO;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The UserIdRecovery2 servlet is third page shown
 *  to user in the user id recovery process
 */
public class UserIdRecovery2 extends HttpServlet {
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

		if (request.getParameter("cancel") != null) {
			response.sendRedirect(response
					.encodeRedirectUrl("Login"));
			return;
		}
		HttpSession session = request.getSession(true);
		ArrayList<String> idsRecovered = (ArrayList<String>) session.getAttribute("userIds2");
		ArrayList<String> answers = (ArrayList<String>) session.getAttribute("secretquesAnswersList");
		
		String answer = request.getParameter("idRecovery2ans");
		StringBuffer errorsSB = new StringBuffer("");
		if (isEmpty(answer)) {
			errorsSB.append("Please provide answer!");
		}
		String strtmp = request.getParameter("idRecoveryq3dropdown");
		if(strtmp.equalsIgnoreCase("select"))
		{
			errorsSB.append("<br>Please select a question");
		}
		if(errorsSB.length()>0)
		{
			request.setAttribute("UserIdRecovery2Errors",errorsSB.toString());
	doGet(request, response);
	return;
		}
		String errors = "";
		if (answer.equalsIgnoreCase(answers.get(Integer.parseInt(request.getParameter("idRecoveryq3dropdown"))))) {
			errors = "Your user id is: \"<font color=\"green\">"
				+ idsRecovered.get(Integer.parseInt(request.getParameter("idRecoveryq3dropdown")))
					+ "</font>\" Please note it down or email it to"
					+ " yourself so that you do not lose it again";
			session.setAttribute("displayUserIdmsg", errors);
			session.setAttribute("userId", idsRecovered.get(Integer.parseInt(request.getParameter("idRecoveryq3dropdown"))));
			response.sendRedirect(response
					.encodeRedirectUrl("UserIdRecovery3"));

		} else {
			errors = "That seems to be incorrect. Please try again!";
			String failedAttempts = (String) session
					.getAttribute("noOfFailedAttempts2");
			if (failedAttempts == null)
				session
						.setAttribute("noOfFailedAttempts2", Integer
								.toString(1));
			else {
				try {
					int tmp = Integer.parseInt(failedAttempts);
					tmp += 1;
					if (tmp >= 5) {
						logger
								.info("no of failed attempts exceeded 5-redirecting to ForgotId");
						response
								.sendRedirect(response
										.encodeRedirectUrl("ForgotId"));
					}
					session.setAttribute("noOfFailedAttempts2", Integer
							.toString(tmp));
				} catch (NumberFormatException nfe) {
					logger.error("NumberFormatException");
				}
			}
		}
		request.setAttribute("UserIdRecovery2Errors", errors);
		doGet(request, response);
		return;
	}

	private void getUsersOwnQuestion(HttpServletRequest request,ArrayList<String> idsRecovered ) {
		connection.getUsersOwnQuestion(request,idsRecovered );
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		ArrayList<String> idsRecovered = (ArrayList<String>) session.getAttribute("userIds2");
		getUsersOwnQuestion(request,idsRecovered);
		ArrayList<String> questions = (ArrayList<String>) session.getAttribute("secretquestionsList");
		ArrayList<String> answers = (ArrayList<String>) session.getAttribute("secretquesAnswersList");
		
			
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>UserId Recovery</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>UserId Recovery</h3>");
		String errors = (String) request.getAttribute("UserIdRecovery2Errors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<form name='idRecoveryForm2' action='' method='post'>");
		sb
				.append("<label> Please provide answer for this secret question</label><br><br>");
		sb.append("<table>");
		sb.append("<tr><td>Question:</td><td>");
		sb.append("<select name='idRecoveryq3dropdown' id='idRecoveryq3dropdown'>");
		sb.append("<option value='select'>Select</option>");
		for (int i = 0; i < questions.size(); i++) {
			sb.append("<option value='" +i
					+ "'>" +questions.get(i) + "</option>");
		}
		sb.append("</select>");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("idRecovery2ans"))) {
			sb
					.append("<input type='text' size='30' name='idRecovery2ans'>&nbsp");
		} else {
			sb
					.append("<input type='text' size='30' name='idRecovery2ans' value='"
							+ request.getParameter("idRecovery2ans")
							+ "'>&nbsp");
		}
		sb.append("</td></tr></table><br>");

		sb.append("<table><tr><td>");
		sb.append("<input type='submit' name='submit' value='Submit'>&nbsp");
		sb.append("</td><td>");
		sb.append("<input type='submit' name='cancel' value='Cancel'>&nbsp");
		sb.append("</td></tr></table>");
		sb.append("<table><tr><td>");
		sb.append("(*) asterisk indicates mandatory field");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("</form><br><br>");
		return sb.toString();
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

}
