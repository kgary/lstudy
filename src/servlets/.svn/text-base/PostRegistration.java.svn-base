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
 * The PostRegistration servlet is page shown to user
 * upon successful registration
 */
public class PostRegistration extends HttpServlet {
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
		String registartion = (String) session
				.getAttribute("registartionsuccess");
		if (registartion != null) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/static/ASU_Header.html");
			dispatcher.include(request, response);
			out.println(generateHtml(request));
			dispatcher = request
					.getRequestDispatcher("/static/ASU_Footer.html");
			dispatcher.include(request, response);
		} else {
			response.sendRedirect(response
					.encodeRedirectUrl("Registration"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String applicationContext = "";
		String userid = (String) session.getAttribute("loggedIn");
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>Post Registartion</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/Logout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<h3 id='pagetitle'>Post Registration</h3>");
		
		sb.append("<br><table align='center'><tr><td>");
		sb.append("Thank you for registering for the longitudinal study! This study" +
				" will ask you, once per year, to take a brief survey to get your impression " +
				"about your education and your professional career. These results will help us " +
				"understand whether the Software Enterprise is achieving its goals of producing" +
				" industry-ready software engineers.<br><br>We are currently seeking volunteers to " +
				"help us with a deeper more advanced assessment. If you have 30 minutes to volunteer, " +
				"please email Dr. Gary at kgary@asu.edu.<br><br>We are also looking for your professional " +
				"supervisor/mentor to participate if s/he has the time. We would like her/him to take a " +
				"short survey as well, so if you think your mentor would be willing, please email Dr. Gary. ");
		sb.append("</td></tr></table><br>");
		
		String errors = (String) session.getAttribute("registartionsuccess")
				+ "<br> Your userid is \"" + userid
				+ "\" .Please make a note of it.";
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<br><table align='center'><tr><td>");
		sb
				.append("<a href='"+applicationContext+"/LandingPageForUser'>Click Here</a>"
						+ " to goto Landing page");
		sb.append("</td></tr></table><br><br><br>");
		return sb.toString();
	}

}
