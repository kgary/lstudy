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

import properties.PropertiesClass;

import util.LoggerUtil;
import dao.ConnectionDAO;

/**
 * Servlet implementation class UserIdRecoveryFromSQ
 */
public class UserIdRecoveryFromSQ extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	private static Logger logger = LoggerUtil.getClassLogger();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/static/ASU_Header.html");
		dispatcher.include(request, response);
		out.println(generateHtml(request));
		dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
		dispatcher.include(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("cancel") != null) {
			response.sendRedirect(response
					.encodeRedirectUrl("Login"));
			return;
		}
		
		HttpSession session = request.getSession(true);
		
		String question = request.getParameter("idRecoveryFSQdropdown");
		String answer = request.getParameter("idRecoveryFSQans");
		
		StringBuffer errors = new StringBuffer("");
		if(question==null)
		{
			errors.append("Please select a question<br>");
		}
		if(answer ==null || answer.length()==0)
		{
			errors.append("Please provide your answer<br>");
		}
		
		if(errors.length()>0)
		{
			request.setAttribute("UserIdRecoveryFSQerrors", errors.toString());
			doGet(request, response);
			return;
		}
		
		logger.info("question selected: " + question);
		logger.info("answer given: " + answer);
		
		String userid = checkIfSecretQandACombinationIsValid(question,answer);
		
		if(userid !="" && userid.length() > 0)
		{
			session.setAttribute("userId", userid);
			String message = "Your user id is: \"<font color=\"green\">" + userid
			+ "</font>\" Please note it down or email it to"
			+ " yourself so that you do not lose it again";
	session.setAttribute("displayUserIdmsg", message);
			response.sendRedirect(response
					.encodeRedirectUrl("UserIdRecovery3"));
		}
		else
		{
			String failedAttempts = (String) session
			.getAttribute("noOfFailedAttemptsFSQ");
	if (failedAttempts == null)
		session.setAttribute("noOfFailedAttemptsFSQ", Integer.toString(1));
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
				return;
			}
			session.setAttribute("noOfFailedAttemptsFSQ", Integer
					.toString(tmp));
		} catch (NumberFormatException nfe) {
			logger.error("NumberFormatException");
		}
		
		
	}
	StringBuffer sb = new StringBuffer("");
	sb.append("Unfortunately thats an incorrect answer.Please try again<br>");
	request
	.setAttribute(
			"UserIdRecoveryFSQerrors",sb.toString());	
	doGet(request, response);
	return;
		}
		
		
	}
	
	private String checkIfSecretQandACombinationIsValid(String question,String ans)
	{
		String userid = connection.checkIfSecretQandACombinationIsValid(question, ans);
		return userid;
	}
	
	private ArrayList<String> getQuestionFromDB() {
		ArrayList<String> questionsList = connection.getAllSecretQuestionsFromDb();
		return questionsList;
	}
	
	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}
	
	private String generateHtml(HttpServletRequest request) {
		ArrayList<String> questionsList = getQuestionFromDB();
		
		StringBuilder sb = new StringBuilder("");
		String strTemp = "";
		sb.append("<title>UserId Recovery</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>UserId Recovery</h3>");
		
		String errors = (String) request.getAttribute("UserIdRecoveryFSQerrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color='red'>");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		
		sb.append("<form name='idRecoveryFSQForm' action='' method='post'>");
		sb
		.append("<label> Select your own question and enter answer</label><br><br>");
		sb.append("<table>");
		sb.append("<tr><td>Question:</td><td>");
		sb
		.append("<select size='4' name='idRecoveryFSQdropdown' id='idRecoveryFSQdropdown'>");
		if (isEmpty(request.getParameter("idRecoveryFSQdropdown"))) {
			for (int i = 0; i < questionsList.size(); i++) {
				sb.append("<option value='" + (String) questionsList.get(i)
						+ "'>" + (String) questionsList.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < questionsList.size(); i++) {
				strTemp = (String) questionsList.get(i);
				if (strTemp.equalsIgnoreCase(request
						.getParameter("idRecoveryFSQdropdown"))) {
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
		sb.append("</select>");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("idRecoveryFSQans"))) {
			sb
					.append("<input type='text' size='30' name='idRecoveryFSQans'>&nbsp");
		}else {
			sb
			.append("<input type='text' size='30' name='idRecoveryFSQans' value='"
					+ request.getParameter("idRecoveryFSQans")
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

}
