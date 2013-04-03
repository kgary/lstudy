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

import dao.ConnectionDAO;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The UserIdRecovery1 servlet is second page shown
 *  to user in the user id recovery process
 */
public class UserIdRecovery1 extends HttpServlet {
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

		String ansGivenByUser = request.getParameter("UserIdRecovery1ans1");
		HttpSession session = request.getSession(true);
		if (ansGivenByUser.equalsIgnoreCase((String) session
				.getAttribute("2ndQuestionAns"))) {

			response.sendRedirect(response
					.encodeRedirectUrl("UserIdRecovery2"));
		} else {

			request
					.setAttribute("UserIdRecovery1errors",
							"Unfortunately thats an incorrect answer.<br>Please try again ");
			String failedAttempts = (String) session
					.getAttribute("noOfFailedAttempts1");
			if (failedAttempts == null)
				session
						.setAttribute("noOfFailedAttempts1", Integer
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
					session.setAttribute("noOfFailedAttempts1", Integer
							.toString(tmp));
				} catch (NumberFormatException nfe) {
					logger.error("NumberFormatException");
				}
			}
			doGet(request, response);
			return;

		}
	}

	private String generateHtml(HttpServletRequest request) {
		// errors ="";
		HttpSession session = request.getSession(true);
		getSecondSecretQuestion(request);
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>UserId Recovery</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>UserId Recovery</h3>");
		String errors = (String) request.getAttribute("UserIdRecovery1errors");
		if (!(isEmpty(errors))) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<form name='UserIdRecovery1Form' action='' method='post'>");
		sb
				.append("<label> Please provide answer for this secret question</label><br><br>");
		sb.append("<table>");
		sb.append("<tr><td>Question:</td><td>");
		sb.append((String) session.getAttribute("2ndQuestion") + "&nbsp");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");

		if (isEmpty(request.getParameter("UserIdRecovery1ans1"))) {
			sb
					.append("<input type='text' size='30' name='UserIdRecovery1ans1'>&nbsp");
		} else {
			sb
					.append("<input type='text' size='30' name='UserIdRecovery1ans1' value='"
							+ request.getParameter("UserIdRecovery1ans1")
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

	private void getSecondSecretQuestion(HttpServletRequest request) {
		connection.getSecondSecretQuestion(request);
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

}
