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
 * The UserIdRecovery servlet is first page shown
 *  to user in the user id recovery process
 */
public class UserIdRecovery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	ArrayList questionsList = new ArrayList();
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
		HttpSession session = request.getSession(true);
		if (request.getParameter("cancel") != null) {
			response.sendRedirect(response
					.encodeRedirectUrl("Login"));
			return;
		}
		String question = request.getParameter("idRecoveryq1dropdown");
		String answer = request.getParameter("idRecoveryans1");
		
		StringBuffer errors = new StringBuffer("");
		if(question.equalsIgnoreCase("select"))
		{
			errors.append("Please select a question<br>");
		}
		if(answer ==null || answer.length()==0)
		{
			errors.append("Please provide your answer<br>");
		}
		
		if(errors.length()>0)
		{
			request.setAttribute("UserIdRecoveryerrors", errors.toString());
			doGet(request, response);
			return;
		}
		
		logger.info("question selected: " + question);
		logger.info("answer given: " + answer);
		ArrayList<String> userIds = checkIfQuesAnsCombinationIsValid(question, answer);
		if (userIds != null && userIds.size()>0) {
			if(userIds.size()==1)
			{
				session.setAttribute("userId", userIds.get(0));
				String message = "Your user id is: \"<font color=\"green\">" + userIds.get(0)
				+ "</font>\" Please note it down or email it to"
				+ " yourself so that you do not lose it again";
		session.setAttribute("displayUserIdmsg", message);
				response.sendRedirect(response
						.encodeRedirectUrl("UserIdRecovery3"));
			}
			else
			{
				String isSecond = (String) request.getParameter("secondQuestion");
				if(isSecond !=null)
				{
					ArrayList<String> userIdFromQuestion1 = (ArrayList<String>) session.getAttribute("userIds1");
					ArrayList<String> idsRecovered= new ArrayList<String>();
					if(userIdFromQuestion1 !=null && userIdFromQuestion1.size()>0)
					{
						
						for(int i=0;i<userIdFromQuestion1.size();i++)
						{
							for(int j=0;j<userIds.size();j++)
							{
								if(userIds.get(j).trim().equalsIgnoreCase(userIdFromQuestion1.get(i).trim()))
								{
									idsRecovered.add(userIds.get(j));
									break;
								}
							}
						}
					}
					if(idsRecovered.size()==1)
					{
						String message = "Your user id is: \"<font color=\"green\">" + idsRecovered.get(0)
						+ "</font>\" Please note it down or email it to"
						+ " yourself so that you do not lose it again";
				session.setAttribute("displayUserIdmsg", message);
						response.sendRedirect(response
								.encodeRedirectUrl("UserIdRecovery3"));
					}
					else
					{
						if(idsRecovered.size()>0)
						session.setAttribute("userIds2", idsRecovered);
						else
							session.setAttribute("userIds2", userIds);
						response.sendRedirect(response
								.encodeRedirectUrl("UserIdRecovery2"));
					}
				}
				else
				{
				session.setAttribute("userIds1", userIds);
				response.sendRedirect(response
						.encodeRedirectUrl("UserIdRecovery?secondQuestion=true"));
				}
			}
			
			
		} else {
			/*request
					.setAttribute(
							"UserIdRecoveryerrors",
							"Unfortunately thats an incorrect answer.<br>Please try again");*/

			String failedAttempts = (String) session
					.getAttribute("noOfFailedAttempts");
			if (failedAttempts == null)
				session.setAttribute("noOfFailedAttempts", Integer.toString(1));
			else {
				try {
					int tmp = Integer.parseInt(failedAttempts);
					tmp += 1;
					if (tmp >= 3) {
						StringBuffer sb = new StringBuffer("");
						sb.append("Unfortunately thats an incorrect answer.<br>");
						sb.append("Please <a href='UserIdRecoveryFromSQ'>Click Here</a> to answer your own secret question");
						request
						.setAttribute(
								"UserIdRecoveryerrors",sb.toString());	
					}
					if (tmp >= 5) {
						logger
								.info("no of failed attempts exceeded 5-redirecting to ForgotId");
						response
								.sendRedirect(response
										.encodeRedirectUrl("ForgotId"));
						return;
					}
					session.setAttribute("noOfFailedAttempts", Integer
							.toString(tmp));
				} catch (NumberFormatException nfe) {
					logger.error("NumberFormatException");
				}
			}
			/*response.sendRedirect(response
					.encodeRedirectUrl("UserIdRecovery?secondQuestion=true"));*/
			request
			.setAttribute(
					"secondQuestion",
					"true")	;
			doGet(request, response);
			return;
		}
	}

	private String generateHtml(HttpServletRequest request) {
		questionsList.clear();
		getQuestionFromDB();
		String strTemp = "";
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>UserId Recovery</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>UserId Recovery</h3>");
		String isSecondQuestion = (String)request.getAttribute("secondQuestion");
		if(isSecondQuestion !=null)
		{
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append("Please answer your second secret question<br>");
			sb.append("</font></td></tr></table>");
			request.setAttribute("2ndques", "true");
		}
		String errors = (String) request.getAttribute("UserIdRecoveryerrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td>");
			sb.append(errors);
			sb.append("</td></tr></table>");
		}
		sb.append("<form name='idRecoveryForm' action='' method='post'>");

		sb
				.append("<label> Select your secret question and enter answer</label><br><br>");
		sb.append("<table>");
		sb.append("<tr><td>Question:</td><td>");
		sb
				.append("<select name='idRecoveryq1dropdown' id='idRecoveryq1dropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(request.getParameter("idRecoveryq1dropdown"))) {
			for (int i = 0; i < questionsList.size(); i++) {
				sb.append("<option value='" + (String) questionsList.get(i)
						+ "'>" + (String) questionsList.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < questionsList.size(); i++) {
				strTemp = (String) questionsList.get(i);
				if (strTemp.equalsIgnoreCase(request
						.getParameter("idRecoveryq1dropdown"))) {
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
		if (isEmpty(request.getParameter("idRecoveryans1"))) {
			sb
					.append("<input type='text' size='30' name='idRecoveryans1'>&nbsp");
		} else {
			sb
					.append("<input type='text' size='30' name='idRecoveryans1' value='"
							+ request.getParameter("idRecoveryans1")
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

	private ArrayList<String> checkIfQuesAnsCombinationIsValid(String question,
			String answer) {
		ArrayList<String> userIds = connection.checkIfQuesAnsCombinationIsValid(question,
				answer);
		return userIds;
	}

	private void getQuestionFromDB() {
		questionsList = connection.getQuestionsFromDb();
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

}
