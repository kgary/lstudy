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

import beans.SurveyBean;

import controllers.EmailController;
import dao.ConnectionDAO;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The InstructionsToAdmin servlet which show instructions
 *  to be followed by admin before triggering emails
 */
public class InstructionsToAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String fromAddress = PropertiesClass.fromEmailAddress;
	private ConnectionDAO connection = new ConnectionDAO();
	private static Logger logger = LoggerUtil.getClassLogger();
	static final String HEXES = "0123456789ABCDEF";
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
		String userId = (String) session.getAttribute("admin");
		String subject = (String) session.getAttribute("subject");
		String body = (String) session.getAttribute("body");
		ArrayList emailIds = (ArrayList) session.getAttribute("emailIds");
		if (userId != null) {
			if (subject == null || body == null || emailIds == null) {
				response.sendRedirect(response
						.encodeRedirectUrl("TriggerEmails"));
			} else {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/static/ASU_Header.html");
				dispatcher.include(request, response);
				out.println(generateHtml(request));
				dispatcher = request
						.getRequestDispatcher("/static/ASU_Footer.html");
				dispatcher.include(request, response);
			}

		} else {
			response.sendRedirect(response
					.encodeRedirectUrl("Admin"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");
		if (userId == null) {
			doGet(request, response);
			return;
		}
		String subject = (String) session.getAttribute("subject");
		String body = (String) session.getAttribute("body");
		String body1 = "";
		CharSequence charSequence = "<emailid>";
		CharSequence charSequence1;
		CharSequence charSequence2 = "[system generated URL]";
		CharSequence charSequence3 = PropertiesClass.hostUrl;
		body = body.replace(charSequence2, charSequence3);
		ArrayList emailIds = (ArrayList) session.getAttribute("emailIds");
		EmailController emailcontroller = new EmailController();
		for (int index = 0; index < emailIds.size(); index++) {
			charSequence1 = encodeEmailAddress((String) emailIds.get(index));
			//charSequence1 = (String) emailIds.get(index);
			body1 = body.replace(charSequence, charSequence1);
			emailcontroller.sendEmail((String) emailIds.get(index),
					fromAddress, subject, body1);
		}

		response.sendRedirect(response
				.encodeRedirectUrl("AdminHome"));
	}
	
	private String encodeEmailAddress(String emailId)
	{
		byte [] raw = emailId.getBytes();
		  if ( raw == null ) {
		      return null;
		    }
		    final StringBuilder hex = new StringBuilder( 2 * raw.length );
		    for ( final byte b : raw ) {
		      hex.append(HEXES.charAt((b & 0xF0) >> 4))
		         .append(HEXES.charAt((b & 0x0F)));
		    }
		    return hex.toString();
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String applicationContext = "";
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>InstructionsToAdmin</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<h3> Admin User - Instructions To Admin</h3>");
		sb.append("<form name='instructsForm' action='' method='post'>");

		String surveySelected = (String) session.getAttribute("selectedSurvey");
		int surveyid = getSurveyId(surveySelected);
		String redirectionurl = getRedirectionUrl(request, surveyid);
		sb
				.append("<ul type='none'><li><label>Please ensure the following things are done"
						+ " before triggering emails</label></li><p></P>");
		sb
				.append("<li><label class='nobold'>1)Enter this url in the redirection url of survey:</label><label> "
						+ redirectionurl + "</label></li><br>");
		sb
				.append("<li><label class='nobold'>2)Ensure that the 'Automatic load url when survey is complete' setting is set to Yes for Survey settings</label></li><br>");
		sb
				.append("<li><label class='nobold'>3)Ensure the survey is deployed and is accessible.</label></li><br>");
		sb
				.append("<li><label class='nobold'>4)Ensure tokens are added to this survey in Limesurvey tool. </label></li><br>");
		sb
				.append("<li> <br> <input type='submit' name='TriggerEmails' value='TriggerEmails'></li>");
		sb.append("<ul></form>");
		sb.append("<br><br><br><br><br><br><br>");
		return sb.toString();
	}

	private String getRedirectionUrl(HttpServletRequest request, int surveyid) {
		String url = PropertiesClass.host;
	//	String url = (request.getRequestURL()).toString();
		//result = url.substring(0, url.indexOf(getServletContext().getServletContextName()));
		url = url+ "/BriefingPage?surveyid=" + surveyid;
		logger.info("Redirection url:" + url);
		return url;
	}

	//gets the survey id for the given survey
	private int getSurveyId(String surveySelected) {
		int surveyId = connection.getSurveyId(surveySelected);
		return surveyId;
	}

}
