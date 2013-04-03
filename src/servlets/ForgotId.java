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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import properties.PropertiesClass;
import util.LoggerUtil;

import controllers.EmailController;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The ForgotId servlet is collect email address of the 
 * user and email him his userid. This servlet is not 
 * used anymore
 */
public class ForgotId extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String admin = "softwareenterprise@asu.edu";
	private static final String fromAddress = "softwareenterprise@asu.edu";
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
	}

	private void sendEmailToAdmin(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("loggedIn");
		StringBuffer body = new StringBuffer();
		if (userId != null) {
			body.append("user " + userId
					+ " has requested for secret questions re-set");
		} else {
			body
					.append("user who forgot both his uerid and secret questions asked for userid recovery");
		}
		EmailController emailcon = new EmailController();
		emailcon.sendEmail(admin, fromAddress,
				"userid/secret questions recovery", body.toString());
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		StringBuilder sb = new StringBuilder("");
		sendEmailToAdmin(request);
		sb.append("<title>UserId Recovery</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>UserId Recovery</h3>");
		sb.append("<table align='center'><tr><td><font color=\"red\">");
		sb
				.append("An email has been sent to admin to reset secret questions for you." +
						"Please try again after sometime");
		sb.append("</font></td></tr></table><br><br>");
		session.invalidate();
		logger.info("number of failed attempts exceeded 5. Email hase been sent to admin and session has been killed successfully.");
		return sb.toString();
	}
}
