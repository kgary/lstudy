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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
 * The AddSurveyDetails servlet is used to save details
 * of the survey to database
 */
public class AddSurveyDetails extends HttpServlet {
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

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");

		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");
		if (userId != null) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/static/ASU_Header.html");
			dispatcher.include(request, response);
			out.println(generateHtml(request));
			dispatcher = request
					.getRequestDispatcher("/static/ASU_Footer.html");
			dispatcher.include(request, response);
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
		String surveyName = request.getParameter("surveyName");
		String surveyUrl = request.getParameter("surveyUrl");
		String surveyDesc = request.getParameter("surveyDesc");
		String errors = validateDetails(surveyName, surveyUrl, surveyDesc);

		if (errors != null && errors.length() > 0) {
			request.setAttribute("addSurveyErrors", errors);
			doGet(request, response);
			return;
		}
		boolean isSaveSuccesful = saveSurveyDetails(surveyName, surveyUrl,
				surveyDesc);
		if (isSaveSuccesful) {
			errors = "save successful!";
		} else {
			errors = "error in saving survey details. Please try again after some time";
		}
		logger.info("survey save status: " + errors);
		request.setAttribute("addSurveyErrors", errors);
		doGet(request, response);
		return;
	}

	private boolean saveSurveyDetails(String surveyName, String surveyUrl,
			String surveyDesc) {
		return connection.saveSurveyDetails(surveyName, surveyUrl, surveyDesc);
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

	private String validateDetails(String surveyName, String surveyUrl,
			String surveyDesc) {
		StringBuffer errorsSb = new StringBuffer("");
		if (isEmpty(surveyName)) {
			errorsSb.append("Please provide Survey Name <br>");
		}
		if (isEmpty(surveyUrl)) {
			errorsSb.append("Please provide Survey URL <br>");
		}
		else
		{
			int responseCode = checkIfURLIsValidOrNot(surveyUrl);
			if(responseCode != 200)
			{
				errorsSb.append("URL is not valid <br>");
			}
		}
		if (isEmpty(surveyDesc)) {
			errorsSb.append("Please provide Survey Description <br>");
		}
		return errorsSb.toString();
	}

	private String generateHtml(HttpServletRequest request) {
		String applicationContext = "";
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>AddSurvey</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<a href='"+applicationContext+"/AdminHome'>Home</a>");
		sb.append("<h3 id='pagetitle'>Add Survey </h3>");
		sb.append("<form name='addSurveyForm' action='' method='post'>");
		String errors = (String) request.getAttribute("addSurveyErrors");
		if (!isEmpty(errors)) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<div id='tblcontent'><br><br>");
		sb.append("<label>SurveyName:</label>");
		if(isEmpty(request.getParameter("surveyName")))
		{
			sb
			.append("<input type='text' name='surveyName' maxlength='50'><label class='star'>*</label>");
		}
		else
		{
			sb
			.append("<input type='text' name='surveyName' value='"+request.getParameter("surveyName")+"' maxlength='100'><label class='star'>*</label>");
		}
		
		sb.append("<br><br><label>SurveyURL&nbsp&nbsp&nbsp:</label>");
		if(isEmpty(request.getParameter("surveyUrl")))
		{
			sb
			.append("<input type='text' name='surveyUrl'><label class='star'>*</label>");	
		}
		else
		{
			sb
			.append("<input type='text' name='surveyUrl' value='"+request.getParameter("surveyUrl")+"'><label class='star'>*</label>");
		}
		
		sb.append("<br><br><label>Survey Description</label><p></p>");
		if(isEmpty(request.getParameter("surveyDesc")))
		{
			sb
			.append("<textarea name='surveyDesc' cols='30' rows='3'></textarea><label class='star'>*</label>");	
		}
		else
		{
			sb
			.append("<textarea name='surveyDesc' cols='30' rows='3'>"+request.getParameter("surveyDesc")+"</textarea><label class='star'>*</label>");
		}
		
		sb
				.append("<br><br><input type='submit' name='alogin' value='Submit'>&nbsp");
		sb.append("<br>(*) asterisk indicates mandatory field");
		sb.append("</div></form>");
		sb.append("<br><br><br>");
		return sb.toString();
	}
	
	//checks if given url is vlaid or not. if yes 
	//it opens a new http connection and checks for header
	//for response code of 200
	private int checkIfURLIsValidOrNot(String urlStr)
	{
		int responseCode =0;
		try {
			URL url = new URL(urlStr);
		 responseCode = ((HttpURLConnection) url.openConnection()).getResponseCode();
			System.out.println("aa= "+responseCode);
		} catch (MalformedURLException e) {
			logger.info("MalformedURLException for url: "+urlStr);
		} catch (IOException e) {
			logger.info("IOException for url: "+urlStr);
		}
		logger.info("returning response code: "+responseCode+" for url: "+urlStr);
		return responseCode;
	}

}
