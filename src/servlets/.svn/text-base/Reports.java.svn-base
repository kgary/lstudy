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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import properties.PropertiesClass;

import beans.FilterParameters;
import beans.SurveyQuestionsBean;

import util.LoggerUtil;
import util.RenderUtils;
import dao.ConnectionDAO;

/**
 * Servlet implementation class Reports
 */
public class Reports extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerUtil.getClassLogger();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");

		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");

		// Process auth
		if (userId == null)  {
			response.sendRedirect(response.encodeRedirectURL("Admin"));
		}

		// Process return from other app for groups redirect
		String fromNsfccli = request.getParameter("a"); 
		if(fromNsfccli != null && fromNsfccli.equals("fromnsfccli"))
		{
			String groupid =  request.getParameter("g");
			int gId = Integer.parseInt(groupid);
			ArrayList<String> ids = RenderUtils.getUsersOfAGroup(gId);
			session.setAttribute("usersMatchingTheFilters", ids);
			session.setAttribute("fromNsfccli", fromNsfccli);
			//connection.deleteUserFromLs();
		}
		
		// Now process the action
		String action = request.getParameter("action");
		if(action == null)
		{
			RequestDispatcher dispatcher = request.getRequestDispatcher("/static/ASU_Header.html");
			dispatcher.include(request, response);
			out.println(generateHtml(request));
			dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
			dispatcher.include(request, response);
			return;
		}

		if(action.equals("exportAll"))
		{
			ArrayList<SurveyQuestionsBean> questions = (ArrayList<SurveyQuestionsBean>)session.getAttribute("allSurveyQuestions");
			FilterParameters filters  = (FilterParameters)session.getAttribute("filters");
			String csvFileContents = RenderUtils.generateCSVContentsOfAllResponses(questions, filters);

			if (csvFileContents == null) {
				out.println("No results were returned for CSV file export.<br>\n");
			}
			else
			{
				byte requestBytes[] = csvFileContents.getBytes();
				ByteArrayInputStream bis = new ByteArrayInputStream(requestBytes);
				Date dateToday = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

				try
				{
					response.reset();
					response.setContentType("application/text");
					response.setHeader("Content-disposition","attachment; filename=" + "LS_" + df.format(dateToday) + ".csv" );
					byte[] buf = new byte[1024];
					int len;
					while ((len = bis.read(buf)) > 0){
						response.getOutputStream().write(buf, 0, len);
					}
					bis.close();
					response.getOutputStream().flush(); 
				}
				catch(Exception ex)
				{
					logger.error("exception caught while generating csv contents: "+ex.getMessage());
					ex.printStackTrace();
				}
				return;
			}
		}
	} 

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		StringBuilder sb = new StringBuilder("");
		ArrayList<SurveyQuestionsBean> questions = RenderUtils.getAllSurveyQuestions();
		session.setAttribute("allSurveyQuestions", questions);
		SurveyQuestionsBean questionBean;
		String applicationContext = "";
		FilterParameters filters = null;
		String fromNsfccli = (String)session.getAttribute("fromNsfccli");

		if(fromNsfccli == null)
			filters = (FilterParameters)session.getAttribute("filters");
		//ArrayList<String> users = (ArrayList<String>)session.getAttribute("usersMatchingTheFilters");
		sb.append("<title>Reports</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");

		sb.append("<h3 id='pagetitle'>Reports</h3>");

		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<a href='"+applicationContext+"AdminHome'>Home</a><br/><br/>");

		if(filters != null)
		{
			int counter=0;
			sb.append("<table>");
			sb.append("<tr><td>Filters Selected:</td></tr>");
			if(!filters.getDegreeProgram().trim().equalsIgnoreCase("select"))
			{
				counter++;
				sb.append("<tr><td>Degree Program: "+filters.getDegreeProgram()+
				"</td></tr>");
			}
			if(!filters.getGpa().trim().equalsIgnoreCase("select"))
			{
				counter++;
				sb.append("<tr><td>GPA: "+filters.getGpa()+
				"</td></tr>");
			}
			if(!filters.getGradYear().trim().equalsIgnoreCase("select"))
			{
				counter++;
				sb.append("<tr><td>Year Graduated: "+filters.getGradYear()+
				"</td></tr>");
			}
			if(filters.getEntCourses() != null)
			{
				String [] entCourses = filters.getEntCourses();

				for (int index = 0; index < entCourses.length; index++) {
					if (entCourses[index].equalsIgnoreCase("CST315"))
					{
						counter++;
						sb.append("<tr><td>CST 315: Yes</td></tr>");
					}

					else if (entCourses[index].equalsIgnoreCase("CST316"))
					{
						counter++;
						sb.append("<tr><td>CST 316: Yes</td></tr>");
					}

					else if (entCourses[index].equalsIgnoreCase("CST415"))
					{
						counter++;
						sb.append("<tr><td>CST 415: Yes</td></tr>");
					}

					else if (entCourses[index].equalsIgnoreCase("CST416"))
					{
						counter++;
						sb.append("<tr><td>CST 416: Yes</td></tr>");
					}

				}
			}

			if(counter == 0)
			{
				sb.append("<tr><td>none. Responses of all users will be shown.</td></tr>");
			}
			sb.append("</table><br><br>");

		}

		sb.append("<table align='left'><tr><td><a href='"+applicationContext
				+"ReportsFilter'>Back To Filters Page</a></td></tr>");

		sb.append("<td><a href='"+applicationContext
				+"Reports?action=exportAll'>Export All responses as CSV</a></td></tr></table><br>");
		sb.append("</table><br>");


		sb.append("<table width='75%' border='1'>");

		if(questions != null)
		{
			for(int i=0;i<questions.size();i++)
			{
				questionBean = questions.get(i);
				sb.append("<tr><td>");
				sb.append("<a href='"+applicationContext+"ReportPerQuestion?action=viewResponse&qid="+questionBean.getQuestionId()+"&gid="+
						questionBean.getGroupId()+"'>"+
						questionBean.getQuestion()+"</a>");
				//sb.append(questionBean.getQuestion());
				sb.append("</td></tr>");
			}
		}
		else
		{
			sb.append("<tr><td>");
			sb.append("Unable to retreive questions. Please try again");
			sb.append("</td></tr>");	
		}
		sb.append("</table><br><br>");

		return sb.toString();

	}

}
