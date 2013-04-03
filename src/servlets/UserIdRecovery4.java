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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import controllers.EmailController;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The UserIdRecovery4 servlet is last page shown
 *  to user in the user id recovery process.
 *  This is not used anymore
 */
public class UserIdRecovery4 extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
		String emailId = request.getParameter("email");
		if (emailId == null || emailId.length() == 0) {
			request.setAttribute("newIdErrors", "Please provide email id");
			doGet(request, response);
			return;
		}

		if (!validateEmailId(emailId)) {
			request.setAttribute("newIdErrors",
					"Email id is Invalid. Please provide a valid email id");
			doGet(request, response);
			return;
		}
		String subject = "new user id";
		String body = "Your new user id is \"test12345\"<br>. Please make a note of it.<br>"
				+ "<br><br>Thank you.<br><br><br>Regards,<br>Admin-LongitudinalStudy.";
		EmailController emailcontroller = new EmailController();
		emailcontroller.sendEmail(emailId, "longitudinalStudyAdmin@asu.edu",
				subject, body);

		request
				.setAttribute("newIdErrors",
						"new userid has been emailed to the address provided.<br> Thank you");
		doGet(request, response);
		return;

	}

	private boolean validateEmailId(String emailId) {
		boolean isEmailValid = false;
		String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(emailId);
		if (m.matches())
			isEmailValid = true;
		return isEmailValid;
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>UserId Recovery</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>UserId Recovery</h3>");
		String errors = (String) request.getAttribute("newIdErrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<form name='newIdEmailForm' action='' method='post'>");
		sb
				.append("<label> Please provide your Email id,so that new userid will be emailed to you</label><br><br>");
		sb.append("<table>");
		sb.append("<tr><td>Email:</td><td>");
		sb.append("<input type='text' size='30' name='email'>&nbsp");
		sb.append("</td></tr></table>");
		sb.append("<table><tr><td>");
		sb.append("<input type='submit' name='submit' value='Submit'>&nbsp");
		sb.append("</td></tr></table>");
		sb.append("</form><br><br>");
		return sb.toString();
	}

}
