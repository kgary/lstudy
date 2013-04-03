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
 * The UserIdRecovery3 servlet is used to display
 * recovered userid
 */
public class UserIdRecovery3 extends HttpServlet {
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
		// TODO Auto-generated method stub
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String userid = (String) session.getAttribute("userId");
		session.setAttribute("loggedIn", userid);
		session.setAttribute("userAuthenticated", "true");
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>UserId Recovery</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<h3 id='pagetitle'>UserId Recovery</h3>");
		String errors = (String) session.getAttribute("displayUserIdmsg");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb
				.append("<a href='LandingPageForUser'>Click Here</a>"
						+ " to goto Landing page");
		return sb.toString();
	}
}
