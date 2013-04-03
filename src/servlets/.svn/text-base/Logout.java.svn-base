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

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The Logout servlet is page shown to user
 * upon logout
 */
public class Logout extends HttpServlet {
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
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("loggedIn");
		if (userId != null) {
			session.invalidate();
			logger.info("session killed succesfully");
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/static/ASU_Header.html");
			dispatcher.include(request, response);
			out.println(generateHtml());
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
	}

	private String generateHtml() {
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>Logout</title>");
		sb.append("<table align='center'><tr><td>");
		sb.append(PropertiesClass.logoutMessage);
		sb.append("</td></tr></table><br><br>");
		sb.append("<table align='center'><tr><td>");
		sb.append("<a href='"+"Login'>Click Here</a>");
		sb.append(" to goto login page");
		sb.append("</td></tr></table><br><br><br><br><br>");
		return sb.toString();
	}

}
